package com.dali.trip.controller;

import com.dali.trip.common.PageResult;
import com.dali.trip.common.Result;
import com.dali.trip.dto.GenerateItineraryRequest;
import com.dali.trip.service.ItineraryService;
import com.dali.trip.vo.ItineraryVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 行程控制器
 */
@RestController
@RequestMapping("/itineraries")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;

    /**
     * 生成行程
     */
    @PostMapping("/generate")
    public Result<ItineraryVO> generateItinerary(@Valid @RequestBody GenerateItineraryRequest request) {
        ItineraryVO itinerary = itineraryService.generateItinerary(request);
        return Result.success(itinerary);
    }

    /**
     * 获取行程详情
     */
    @GetMapping("/{id}")
    public Result<ItineraryVO> getItineraryDetail(@PathVariable Long id) {
        ItineraryVO itinerary = itineraryService.getItineraryDetail(id);
        return Result.success(itinerary);
    }

    /**
     * 保存行程
     */
    @PostMapping("/{id}/save")
    public Result<Map<String, Long>> saveItinerary(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String title = body != null ? body.get("title") : null;
        Long itineraryId = itineraryService.saveItinerary(id, userId, title);
        return Result.success("保存成功", Map.of("itineraryId", itineraryId));
    }

    /**
     * 获取我的行程列表
     */
    @GetMapping("/my")
    public Result<PageResult<ItineraryVO>> getMyItineraries(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<ItineraryVO> result = itineraryService.getMyItineraries(userId, page, size);
        return Result.success(result);
    }

    /**
     * 删除行程
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteItinerary(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        itineraryService.deleteItinerary(id, userId);
        return Result.success("删除成功", null);
    }
}
