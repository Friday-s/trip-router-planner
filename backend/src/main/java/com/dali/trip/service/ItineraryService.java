package com.dali.trip.service;

import com.dali.trip.common.PageResult;
import com.dali.trip.dto.GenerateItineraryRequest;
import com.dali.trip.vo.ItineraryVO;

/**
 * 行程服务接口
 */
public interface ItineraryService {

    /**
     * 生成行程
     */
    ItineraryVO generateItinerary(GenerateItineraryRequest request);

    /**
     * 获取行程详情
     */
    ItineraryVO getItineraryDetail(Long id);

    /**
     * 保存行程
     */
    Long saveItinerary(Long itineraryId, Long userId, String title);

    /**
     * 获取我的行程列表
     */
    PageResult<ItineraryVO> getMyItineraries(Long userId, Integer page, Integer size);

    /**
     * 删除行程
     */
    void deleteItinerary(Long id, Long userId);
}
