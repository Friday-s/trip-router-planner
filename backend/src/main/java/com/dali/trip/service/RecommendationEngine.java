package com.dali.trip.service;

import com.dali.trip.dto.GenerateItineraryRequest;
import com.dali.trip.vo.ItineraryVO;

/**
 * 推荐引擎接口 - 可插拔设计
 */
public interface RecommendationEngine {

    /**
     * 生成行程推荐
     */
    ItineraryVO generateItinerary(GenerateItineraryRequest request);

    /**
     * 获取引擎类型
     */
    String getEngineType();
}
