package com.dali.trip.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行程天实体
 */
@Data
@TableName("itinerary_days")
public class ItineraryDay {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long itineraryId;

    private Integer dayIndex;

    private String theme;

    private String notes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
