package com.dali.trip.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 路线缓存实体
 */
@Data
@TableName("route_cache")
public class RouteCache {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fromPoiId;

    private Long toPoiId;

    private Integer distanceMeter;

    private Integer walkDurationSec;

    private Integer driveDurationSec;

    private Integer transitDurationSec;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
