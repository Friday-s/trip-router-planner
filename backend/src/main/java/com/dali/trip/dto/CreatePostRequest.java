package com.dali.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发布帖子请求
 */
@Data
public class CreatePostRequest {

    @NotNull(message = "行程ID不能为空")
    private Long itineraryId;

    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度应在2-100个字符之间")
    private String title;

    @Size(max = 500, message = "摘要最多500个字符")
    private String summary;
}
