package com.dali.trip.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dali.trip.common.BusinessException;
import com.dali.trip.config.JwtUtil;
import com.dali.trip.dto.LoginRequest;
import com.dali.trip.dto.RegisterRequest;
import com.dali.trip.entity.User;
import com.dali.trip.mapper.UserMapper;
import com.dali.trip.service.AuthService;
import com.dali.trip.vo.LoginVO;
import com.dali.trip.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public UserVO register(RegisterRequest request) {
        // 检查用户名是否存在
        Long usernameCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername())
        );
        if (usernameCount > 0) {
            throw BusinessException.badRequest("用户名已存在");
        }

        // 检查邮箱是否存在
        Long emailCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail())
        );
        if (emailCount > 0) {
            throw BusinessException.badRequest("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setRole("user");
        user.setStatus(1);

        userMapper.insert(user);

        return toUserVO(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        // 查找用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail())
        );

        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("邮箱或密码错误");
        }

        if (user.getStatus() == 0) {
            throw BusinessException.badRequest("账号已被禁用");
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setExpiresIn(86400L);
        loginVO.setUser(toUserVO(user));

        return loginVO;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.unauthorized("用户不存在");
        }
        return toUserVO(user);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
