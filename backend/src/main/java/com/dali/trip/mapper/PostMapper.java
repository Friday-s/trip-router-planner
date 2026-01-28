package com.dali.trip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.trip.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子 Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
