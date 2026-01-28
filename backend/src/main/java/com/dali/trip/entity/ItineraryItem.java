package com.dali.trip.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行程项实体
 */
@Data
@TableName("itinerary_items")
public class ItineraryItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dayId;

    private Long poiId;

    private Integer orderIndex;

    private String startTime;

    private String tips;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
