package com.dali.trip.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子列表 VO
 */
@Data
public class PostVO {

    private Long id;
    private String title;
    private String summary;
    private AuthorVO author;
    private ItinerarySummaryVO itinerary;
    private Integer likeCount;
    private Integer favoriteCount;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime createdAt;

    @Data
    public static class AuthorVO {
        private Long id;
        private String username;
        private String avatar;
    }

    @Data
    public static class ItinerarySummaryVO {
        private Long id;
        private String city;
        private Integer days;
        private String theme;
    }
}
