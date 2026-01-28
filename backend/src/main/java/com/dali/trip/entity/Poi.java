package com.dali.trip.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * POI景点实体
 */
@Data
@TableName(value = "pois", autoResultMap = true)
public class Poi {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private BigDecimal lng;

    private BigDecimal lat;

    private String address;

    private String city;

    private String area;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    private String brief;

    private String coverImage;

    private Integer visitDuration;

    private String openTime;

    private BigDecimal ticketPrice;

    private String bestTime;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> highlights;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> suitableFor;

    private String tips;

    private String difficulty;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
