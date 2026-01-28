package com.dali.trip.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 行程生成请求
 */
@Data
public class GenerateItineraryRequest {

    private String city = "大理";

    @NotNull(message = "天数不能为空")
    @Min(value = 1, message = "天数最少为1天")
    @Max(value = 7, message = "天数最多为7天")
    private Integer days;

    private String theme;

    private String pace = "moderate";  // relaxed / moderate / intensive

    private String travelers;  // solo / couple / family / friends

    private BigDecimal startLng;

    private BigDecimal startLat;
}
