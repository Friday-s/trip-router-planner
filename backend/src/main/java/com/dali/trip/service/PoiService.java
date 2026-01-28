package com.dali.trip.service;

import com.dali.trip.common.PageResult;
import com.dali.trip.entity.Poi;
import com.dali.trip.vo.PoiVO;

import java.util.List;

/**
 * POI 服务接口
 */
public interface PoiService {

    /**
     * 获取 POI 列表
     */
    PageResult<PoiVO> getPoiList(String city, String area, String tag, String keyword, Integer page, Integer size);

    /**
     * 获取 POI 详情
     */
    PoiVO getPoiDetail(Long id);

    /**
     * 获取区域列表
     */
    List<String> getAreas(String city);

    /**
     * 获取标签列表
     */
    List<String> getTags();

    /**
     * 根据条件获取 POI 列表（内部使用）
     */
    List<Poi> getPoisByCondition(String city, String theme, Integer limit);
}
