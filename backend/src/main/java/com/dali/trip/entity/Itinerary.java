package com.dali.trip.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行程实体
 */
@Data
@TableName(value = "itineraries", autoResultMap = true)
public class Itinerary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String city;

    private Integer days;

    private String theme;

    private String title;

    private String summary;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tips;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
