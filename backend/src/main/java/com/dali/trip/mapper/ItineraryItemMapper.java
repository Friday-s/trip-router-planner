package com.dali.trip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.trip.entity.ItineraryItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行程项 Mapper
 */
@Mapper
public interface ItineraryItemMapper extends BaseMapper<ItineraryItem> {
}
