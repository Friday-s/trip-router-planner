package com.dali.trip.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * POI VO
 */
@Data
public class PoiVO {

    private Long id;
    private String name;
    private BigDecimal lng;
    private BigDecimal lat;
    private String address;
    private String city;
    private String area;
    private List<String> tags;
    private String brief;
    private String coverImage;
    private Integer visitDuration;
    private String openTime;
    private BigDecimal ticketPrice;
    private String bestTime;
    private List<String> highlights;
    private List<String> suitableFor;
    private String tips;
    private String difficulty;
}
