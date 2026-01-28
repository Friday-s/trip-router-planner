package com.dali.trip.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dali.trip.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
