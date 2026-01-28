package com.dali.trip.controller;

import com.dali.trip.common.PageResult;
import com.dali.trip.common.Result;
import com.dali.trip.service.PoiService;
import com.dali.trip.vo.PoiVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POI 控制器
 */
@RestController
@RequestMapping("/pois")
@RequiredArgsConstructor
public class PoiController {

    private final PoiService poiService;

    /**
     * 获取 POI 列表
     */
    @GetMapping
    public Result<PageResult<PoiVO>> getPoiList(
            @RequestParam(defaultValue = "大理") String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<PoiVO> result = poiService.getPoiList(city, area, tag, keyword, page, size);
        return Result.success(result);
    }

    /**
     * 获取 POI 详情
     */
    @GetMapping("/{id}")
    public Result<PoiVO> getPoiDetail(@PathVariable Long id) {
        PoiVO poi = poiService.getPoiDetail(id);
        return Result.success(poi);
    }

    /**
     * 获取区域列表
     */
    @GetMapping("/areas")
    public Result<List<String>> getAreas(@RequestParam(defaultValue = "大理") String city) {
        List<String> areas = poiService.getAreas(city);
        return Result.success(areas);
    }

    /**
     * 获取标签列表
     */
    @GetMapping("/tags")
    public Result<List<String>> getTags() {
        List<String> tags = poiService.getTags();
        return Result.success(tags);
    }
}
