package com.dali.trip.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private LocalDateTime createdAt;
}
