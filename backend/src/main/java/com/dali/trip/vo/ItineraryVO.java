package com.dali.trip.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 行程 VO
 */
@Data
public class ItineraryVO {

    private Long itineraryId;
    private String city;
    private Integer days;
    private String theme;
    private String title;
    private String summary;
    private List<DayVO> dayList;
    private List<String> tips;
    private MapDataVO mapData;

    @Data
    public static class DayVO {
        private Integer dayIndex;
        private String theme;
        private String notes;
        private List<ItemVO> items;
    }

    @Data
    public static class ItemVO {
        private Integer orderIndex;
        private String startTime;
        private PoiVO poi;
        private String tips;
        private RouteVO routeToNext;
    }

    @Data
    public static class RouteVO {
        private Integer distanceMeter;
        private Integer walkDurationSec;
        private Integer driveDurationSec;
        private Integer transitDurationSec;
        private String recommendMode;
    }

    @Data
    public static class MapDataVO {
        private BigDecimal[] center;
        private Integer zoom;
        private List<MarkerVO> markers;
        private List<PolylineVO> polylines;
    }

    @Data
    public static class MarkerVO {
        private Long poiId;
        private String name;
        private BigDecimal[] position;
        private Integer dayIndex;
        private Integer orderIndex;
    }

    @Data
    public static class PolylineVO {
        private Long fromPoiId;
        private Long toPoiId;
        private List<BigDecimal[]> path;
        private Integer dayIndex;
    }
}
