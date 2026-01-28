package com.dali.trip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.trip.entity.Like;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞 Mapper
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
}
