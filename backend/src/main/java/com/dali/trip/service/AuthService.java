package com.dali.trip.service;

import com.dali.trip.dto.LoginRequest;
import com.dali.trip.dto.RegisterRequest;
import com.dali.trip.vo.LoginVO;
import com.dali.trip.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     */
    UserVO register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginVO login(LoginRequest request);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUser(Long userId);
}
