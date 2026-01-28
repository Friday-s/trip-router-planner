package com.dali.trip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.trip.entity.Itinerary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行程 Mapper
 */
@Mapper
public interface ItineraryMapper extends BaseMapper<Itinerary> {
}
