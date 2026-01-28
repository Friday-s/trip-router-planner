package com.dali.trip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dali.trip.common.BusinessException;
import com.dali.trip.common.PageResult;
import com.dali.trip.dto.GenerateItineraryRequest;
import com.dali.trip.entity.*;
import com.dali.trip.mapper.*;
import com.dali.trip.service.ItineraryService;
import com.dali.trip.service.RecommendationEngine;
import com.dali.trip.vo.ItineraryVO;
import com.dali.trip.vo.PoiVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 行程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {

    private final RecommendationEngine recommendationEngine;
    private final ItineraryMapper itineraryMapper;
    private final ItineraryDayMapper itineraryDayMapper;
    private final ItineraryItemMapper itineraryItemMapper;
    private final PoiMapper poiMapper;

    @Override
    @Transactional
    public ItineraryVO generateItinerary(GenerateItineraryRequest request) {
        // 调用推荐引擎生成行程
        ItineraryVO itineraryVO = recommendationEngine.generateItinerary(request);

        // 保存到数据库（临时行程，user_id 为空）
        Itinerary itinerary = new Itinerary();
        itinerary.setCity(request.getCity());
        itinerary.setDays(request.getDays());
        itinerary.setTheme(request.getTheme());
        itinerary.setTitle(itineraryVO.getTitle());
        itinerary.setSummary(itineraryVO.getSummary());
        itinerary.setTips(itineraryVO.getTips());
        itinerary.setStatus(1);

        itineraryMapper.insert(itinerary);

        // 保存行程天和行程项
        if (itineraryVO.getDayList() != null) {
            for (ItineraryVO.DayVO dayVO : itineraryVO.getDayList()) {
                ItineraryDay day = new ItineraryDay();
                day.setItineraryId(itinerary.getId());
                day.setDayIndex(dayVO.getDayIndex());
                day.setTheme(dayVO.getTheme());
                day.setNotes(dayVO.getNotes());

                itineraryDayMapper.insert(day);

                if (dayVO.getItems() != null) {
                    for (ItineraryVO.ItemVO itemVO : dayVO.getItems()) {
                        ItineraryItem item = new ItineraryItem();
                        item.setDayId(day.getId());
                        item.setPoiId(itemVO.getPoi().getId());
                        item.setOrderIndex(itemVO.getOrderIndex());
                        item.setStartTime(itemVO.getStartTime());
                        item.setTips(itemVO.getTips());

                        itineraryItemMapper.insert(item);
                    }
                }
            }
        }

        itineraryVO.setItineraryId(itinerary.getId());
        return itineraryVO;
    }

    @Override
    public ItineraryVO getItineraryDetail(Long id) {
        Itinerary itinerary = itineraryMapper.selectById(id);
        if (itinerary == null || itinerary.getStatus() == 0) {
            throw BusinessException.notFound("行程不存在");
        }

        return buildItineraryVO(itinerary);
    }

    @Override
    @Transactional
    public Long saveItinerary(Long itineraryId, Long userId, String title) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw BusinessException.notFound("行程不存在");
        }

        // 更新 user_id 和 title
        itinerary.setUserId(userId);
        if (title != null && !title.isEmpty()) {
            itinerary.setTitle(title);
        }

        itineraryMapper.updateById(itinerary);

        return itinerary.getId();
    }

    @Override
    public PageResult<ItineraryVO> getMyItineraries(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Itinerary::getUserId, userId)
               .eq(Itinerary::getStatus, 1)
               .orderByDesc(Itinerary::getCreatedAt);

        Page<Itinerary> pageResult = itineraryMapper.selectPage(new Page<>(page, size), wrapper);

        List<ItineraryVO> voList = pageResult.getRecords().stream()
                .map(this::buildSimpleItineraryVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, pageResult.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void deleteItinerary(Long id, Long userId) {
        Itinerary itinerary = itineraryMapper.selectById(id);
        if (itinerary == null) {
            throw BusinessException.notFound("行程不存在");
        }

        if (!userId.equals(itinerary.getUserId())) {
            throw BusinessException.forbidden("无权删除该行程");
        }

        itinerary.setStatus(0);
        itineraryMapper.updateById(itinerary);
    }

    private ItineraryVO buildItineraryVO(Itinerary itinerary) {
        ItineraryVO vo = new ItineraryVO();
        vo.setItineraryId(itinerary.getId());
        vo.setCity(itinerary.getCity());
        vo.setDays(itinerary.getDays());
        vo.setTheme(itinerary.getTheme());
        vo.setTitle(itinerary.getTitle());
        vo.setSummary(itinerary.getSummary());
        vo.setTips(itinerary.getTips());

        // 查询行程天
        List<ItineraryDay> days = itineraryDayMapper.selectList(
                new LambdaQueryWrapper<ItineraryDay>()
                        .eq(ItineraryDay::getItineraryId, itinerary.getId())
                        .orderByAsc(ItineraryDay::getDayIndex)
        );

        List<ItineraryVO.DayVO> dayList = new ArrayList<>();
        List<ItineraryVO.MarkerVO> markers = new ArrayList<>();
        List<ItineraryVO.PolylineVO> polylines = new ArrayList<>();

        for (ItineraryDay day : days) {
            ItineraryVO.DayVO dayVO = new ItineraryVO.DayVO();
            dayVO.setDayIndex(day.getDayIndex());
            dayVO.setTheme(day.getTheme());
            dayVO.setNotes(day.getNotes());

            // 查询行程项
            List<ItineraryItem> items = itineraryItemMapper.selectList(
                    new LambdaQueryWrapper<ItineraryItem>()
                            .eq(ItineraryItem::getDayId, day.getId())
                            .orderByAsc(ItineraryItem::getOrderIndex)
            );

            List<ItineraryVO.ItemVO> itemList = new ArrayList<>();
            Poi prevPoi = null;

            for (ItineraryItem item : items) {
                Poi poi = poiMapper.selectById(item.getPoiId());
                if (poi == null) continue;

                ItineraryVO.ItemVO itemVO = new ItineraryVO.ItemVO();
                itemVO.setOrderIndex(item.getOrderIndex());
                itemVO.setStartTime(item.getStartTime());
                itemVO.setTips(item.getTips());

                PoiVO poiVO = new PoiVO();
                BeanUtils.copyProperties(poi, poiVO);
                itemVO.setPoi(poiVO);

                // 添加地图标记
                ItineraryVO.MarkerVO marker = new ItineraryVO.MarkerVO();
                marker.setPoiId(poi.getId());
                marker.setName(poi.getName());
                marker.setPosition(new BigDecimal[]{poi.getLng(), poi.getLat()});
                marker.setDayIndex(day.getDayIndex());
                marker.setOrderIndex(item.getOrderIndex());
                markers.add(marker);

                // 添加路线
                if (prevPoi != null && !itemList.isEmpty()) {
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

                itemList.add(itemVO);
                prevPoi = poi;
            }

            dayVO.setItems(itemList);
            dayList.add(dayVO);
        }

        vo.setDayList(dayList);

        // 构建地图数据
        ItineraryVO.MapDataVO mapData = new ItineraryVO.MapDataVO();
        if (!markers.isEmpty()) {
            mapData.setCenter(markers.get(0).getPosition());
        } else {
            mapData.setCenter(new BigDecimal[]{new BigDecimal("100.1653"), new BigDecimal("25.6969")});
        }
        mapData.setZoom(12);
        mapData.setMarkers(markers);
        mapData.setPolylines(polylines);
        vo.setMapData(mapData);

        return vo;
    }

    private ItineraryVO buildSimpleItineraryVO(Itinerary itinerary) {
        ItineraryVO vo = new ItineraryVO();
        vo.setItineraryId(itinerary.getId());
        vo.setCity(itinerary.getCity());
        vo.setDays(itinerary.getDays());
        vo.setTheme(itinerary.getTheme());
        vo.setTitle(itinerary.getTitle());
        return vo;
    }
}
