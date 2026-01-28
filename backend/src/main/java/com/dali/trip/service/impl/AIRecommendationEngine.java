package com.dali.trip.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dali.trip.common.BusinessException;
import com.dali.trip.dto.GenerateItineraryRequest;
import com.dali.trip.entity.Poi;
import com.dali.trip.service.PoiService;
import com.dali.trip.service.RecommendationEngine;
import com.dali.trip.vo.ItineraryVO;
import com.dali.trip.vo.PoiVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * AI 推荐引擎实现 - 使用 DeepSeek API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIRecommendationEngine implements RecommendationEngine {

    private final PoiService poiService;

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.timeout}")
    private Integer timeout;

    @Override
    public ItineraryVO generateItinerary(GenerateItineraryRequest request) {
        // 1. 获取候选 POI
        List<Poi> candidatePois = poiService.getPoisByCondition(
                request.getCity(),
                request.getTheme(),
                50
        );

        if (candidatePois.isEmpty()) {
            throw BusinessException.badRequest("该城市暂无景点数据");
        }

        // 2. 构建 AI Prompt
        String prompt = buildPrompt(request, candidatePois);

        // 3. 调用 DeepSeek API
        String aiResponse = callDeepSeekApi(prompt);

        // 4. 解析 AI 响应并构建行程
        return parseAiResponse(aiResponse, candidatePois, request);
    }

    @Override
    public String getEngineType() {
        return "ai";
    }

    private String buildPrompt(GenerateItineraryRequest request, List<Poi> pois) {
        StringBuilder sb = new StringBuilder();

        sb.append("你是一个专业的旅行规划师，请根据以下信息为用户规划一个").append(request.getDays()).append("日游行程。\n\n");

        sb.append("## 旅行信息\n");
        sb.append("- 目的地：").append(request.getCity()).append("\n");
        sb.append("- 天数：").append(request.getDays()).append("天\n");
        if (request.getTheme() != null) {
            sb.append("- 主题偏好：").append(request.getTheme()).append("\n");
        }
        sb.append("- 节奏：").append(getPaceDescription(request.getPace())).append("\n");
        if (request.getTravelers() != null) {
            sb.append("- 出行人群：").append(getTravelersDescription(request.getTravelers())).append("\n");
        }

        sb.append("\n## 可选景点列表\n");
        for (int i = 0; i < pois.size(); i++) {
            Poi poi = pois.get(i);
            sb.append(i + 1).append(". ").append(poi.getName());
            sb.append(" (区域:").append(poi.getArea());
            sb.append(", 游玩时长:").append(poi.getVisitDuration()).append("分钟");
            if (poi.getTags() != null) {
                sb.append(", 标签:").append(String.join("/", poi.getTags()));
            }
            sb.append(")\n");
        }

        sb.append("\n## 输出要求\n");
        sb.append("请以JSON格式输出行程安排，格式如下：\n");
        sb.append("```json\n");
        sb.append("{\n");
        sb.append("  \"title\": \"行程标题\",\n");
        sb.append("  \"summary\": \"行程概述\",\n");
        sb.append("  \"tips\": [\"贴士1\", \"贴士2\"],\n");
        sb.append("  \"days\": [\n");
        sb.append("    {\n");
        sb.append("      \"dayIndex\": 1,\n");
        sb.append("      \"theme\": \"当日主题\",\n");
        sb.append("      \"notes\": \"当日备注\",\n");
        sb.append("      \"items\": [\n");
        sb.append("        {\"poiIndex\": 1, \"startTime\": \"09:00\", \"tips\": \"游玩建议\"}\n");
        sb.append("      ]\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("```\n");
        sb.append("注意：poiIndex 是上面景点列表的序号（从1开始）。\n");
        sb.append("请确保每天安排合理，考虑景点之间的距离和游玩时长。只输出JSON，不要有其他内容。");

        return sb.toString();
    }

    private String callDeepSeekApi(String prompt) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", model);
            requestBody.set("messages", JSONUtil.parseArray("[{\"role\":\"user\",\"content\":" + JSONUtil.quote(prompt) + "}]"));
            requestBody.set("temperature", 0.7);

            HttpResponse response = HttpRequest.post(baseUrl + "/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(timeout)
                    .execute();

            if (!response.isOk()) {
                log.error("DeepSeek API error: {}", response.body());
                throw new BusinessException("AI服务调用失败，请稍后重试");
            }

            JSONObject responseJson = JSONUtil.parseObj(response.body());
            return responseJson.getByPath("choices[0].message.content", String.class);
        } catch (Exception e) {
            log.error("DeepSeek API call failed", e);
            throw new BusinessException("行程生成失败，请稍后重试");
        }
    }

    private ItineraryVO parseAiResponse(String aiResponse, List<Poi> pois, GenerateItineraryRequest request) {
        try {
            // 提取 JSON
            String json = extractJson(aiResponse);
            JSONObject responseObj = JSONUtil.parseObj(json);

            ItineraryVO itinerary = new ItineraryVO();
            itinerary.setCity(request.getCity());
            itinerary.setDays(request.getDays());
            itinerary.setTheme(request.getTheme());
            itinerary.setTitle(responseObj.getStr("title", request.getCity() + request.getDays() + "日游"));
            itinerary.setSummary(responseObj.getStr("summary"));

            JSONArray tipsArray = responseObj.getJSONArray("tips");
            if (tipsArray != null) {
                itinerary.setTips(tipsArray.toList(String.class));
            }

            // 解析天
            List<ItineraryVO.DayVO> dayList = new ArrayList<>();
            List<ItineraryVO.MarkerVO> markers = new ArrayList<>();
            List<ItineraryVO.PolylineVO> polylines = new ArrayList<>();

            JSONArray daysArray = responseObj.getJSONArray("days");
            if (daysArray != null) {
                for (int i = 0; i < daysArray.size(); i++) {
                    JSONObject dayObj = daysArray.getJSONObject(i);
                    ItineraryVO.DayVO day = new ItineraryVO.DayVO();
                    day.setDayIndex(dayObj.getInt("dayIndex", i + 1));
                    day.setTheme(dayObj.getStr("theme"));
                    day.setNotes(dayObj.getStr("notes"));

                    List<ItineraryVO.ItemVO> items = new ArrayList<>();
                    JSONArray itemsArray = dayObj.getJSONArray("items");
                    Poi prevPoi = null;

                    if (itemsArray != null) {
                        for (int j = 0; j < itemsArray.size(); j++) {
                            JSONObject itemObj = itemsArray.getJSONObject(j);
                            int poiIndex = itemObj.getInt("poiIndex", 1) - 1;

                            if (poiIndex >= 0 && poiIndex < pois.size()) {
                                Poi poi = pois.get(poiIndex);

                                ItineraryVO.ItemVO item = new ItineraryVO.ItemVO();
                                item.setOrderIndex(j + 1);
                                item.setStartTime(itemObj.getStr("startTime"));
                                item.setTips(itemObj.getStr("tips"));

                                PoiVO poiVO = new PoiVO();
                                BeanUtils.copyProperties(poi, poiVO);
                                item.setPoi(poiVO);

                                // 添加路线信息
                                if (prevPoi != null) {
                                    item.setRouteToNext(null);
                                    // 上一个 item 的 routeToNext
                                    if (!items.isEmpty()) {
                                        ItineraryVO.RouteVO route = calculateRoute(prevPoi, poi);
                                        items.get(items.size() - 1).setRouteToNext(route);

                                        // 添加 polyline
                                        ItineraryVO.PolylineVO polyline = new ItineraryVO.PolylineVO();
                                        polyline.setFromPoiId(prevPoi.getId());
                                        polyline.setToPoiId(poi.getId());
                                        polyline.setPath(Arrays.asList(
                                                new BigDecimal[]{prevPoi.getLng(), prevPoi.getLat()},
                                                new BigDecimal[]{poi.getLng(), poi.getLat()}
                                        ));
                                        polyline.setDayIndex(day.getDayIndex());
                                        polylines.add(polyline);
                                    }
                                }

                                items.add(item);

                                // 添加地图标记
                                ItineraryVO.MarkerVO marker = new ItineraryVO.MarkerVO();
                                marker.setPoiId(poi.getId());
                                marker.setName(poi.getName());
                                marker.setPosition(new BigDecimal[]{poi.getLng(), poi.getLat()});
                                marker.setDayIndex(day.getDayIndex());
                                marker.setOrderIndex(j + 1);
                                markers.add(marker);

                                prevPoi = poi;
                            }
                        }
                    }

                    day.setItems(items);
                    dayList.add(day);
                }
            }

            itinerary.setDayList(dayList);

            // 构建地图数据
            ItineraryVO.MapDataVO mapData = new ItineraryVO.MapDataVO();
            if (!markers.isEmpty()) {
                BigDecimal[] firstPos = markers.get(0).getPosition();
                mapData.setCenter(firstPos);
            } else {
                mapData.setCenter(new BigDecimal[]{new BigDecimal("100.1653"), new BigDecimal("25.6969")});
            }
            mapData.setZoom(12);
            mapData.setMarkers(markers);
            mapData.setPolylines(polylines);

            itinerary.setMapData(mapData);

            return itinerary;
        } catch (Exception e) {
            log.error("Parse AI response failed", e);
            throw new BusinessException("行程解析失败，请重试");
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private ItineraryVO.RouteVO calculateRoute(Poi from, Poi to) {
        // 简单估算（后续可接入高德API）
        double distance = calculateDistance(
                from.getLat().doubleValue(), from.getLng().doubleValue(),
                to.getLat().doubleValue(), to.getLng().doubleValue()
        );

        ItineraryVO.RouteVO route = new ItineraryVO.RouteVO();
        route.setDistanceMeter((int) distance);
        route.setWalkDurationSec((int) (distance / 1.2)); // 步行约 1.2m/s
        route.setDriveDurationSec((int) (distance / 8.0)); // 驾车约 8m/s
        route.setTransitDurationSec((int) (distance / 4.0)); // 公交约 4m/s

        // 推荐交通方式
        if (distance < 1500) {
            route.setRecommendMode("walk");
        } else if (distance < 5000) {
            route.setRecommendMode("transit");
        } else {
            route.setRecommendMode("drive");
        }

        return route;
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000; // 地球半径（米）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String getPaceDescription(String pace) {
        return switch (pace) {
            case "relaxed" -> "轻松悠闲，每天2-3个景点";
            case "intensive" -> "紧凑充实，每天4-5个景点";
            default -> "适中节奏，每天3-4个景点";
        };
    }

    private String getTravelersDescription(String travelers) {
        return switch (travelers) {
            case "solo" -> "独自旅行";
            case "couple" -> "情侣出游";
            case "family" -> "家庭亲子";
            case "friends" -> "朋友结伴";
            default -> travelers;
        };
    }
}
