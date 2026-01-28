package com.dali.trip.controller;

import com.dali.trip.common.PageResult;
import com.dali.trip.common.Result;
import com.dali.trip.dto.CreatePostRequest;
import com.dali.trip.service.PostService;
import com.dali.trip.vo.PostVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 帖子控制器
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 获取帖子列表
     */
    @GetMapping
    public Result<PageResult<PostVO>> getPostList(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<PostVO> result = postService.getPostList(sort, page, size, userId);
        return Result.success(result);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<PostVO> getPostDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVO post = postService.getPostDetail(id, userId);
        return Result.success(post);
    }

    /**
     * 发布帖子
     */
    @PostMapping
    public Result<Map<String, Long>> createPost(
            @Valid @RequestBody CreatePostRequest createRequest,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long postId = postService.createPost(createRequest, userId);
        return Result.success("发布成功", Map.of("postId", postId));
    }

    /**
     * 点赞帖子
     */
    @PutMapping("/{id}/like")
    public Result<PostVO> likePost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVO result = postService.likePost(id, userId);
        return Result.success("点赞成功", result);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{id}/like")
    public Result<PostVO> unlikePost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVO result = postService.unlikePost(id, userId);
        return Result.success("取消点赞成功", result);
    }

    /**
     * 收藏帖子
     */
    @PutMapping("/{id}/favorite")
    public Result<PostVO> favoritePost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVO result = postService.favoritePost(id, userId);
        return Result.success("收藏成功", result);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}/favorite")
    public Result<PostVO> unfavoritePost(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PostVO result = postService.unfavoritePost(id, userId);
        return Result.success("取消收藏成功", result);
    }

    /**
     * 获取我发布的帖子
     */
    @GetMapping("/my")
    public Result<PageResult<PostVO>> getMyPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<PostVO> result = postService.getMyPosts(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我收藏的帖子
     */
    @GetMapping("/favorites")
    public Result<PageResult<PostVO>> getMyFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<PostVO> result = postService.getMyFavorites(userId, page, size);
        return Result.success(result);
    }
}
