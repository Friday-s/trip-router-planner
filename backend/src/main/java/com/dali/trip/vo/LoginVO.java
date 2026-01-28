package com.dali.trip.vo;

import lombok.Data;

/**
 * 登录响应 VO
 */
@Data
public class LoginVO {

    private String token;
    private Long expiresIn;
    private UserVO user;
}
