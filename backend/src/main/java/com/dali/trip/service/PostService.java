package com.dali.trip.service;

import com.dali.trip.common.PageResult;
import com.dali.trip.dto.CreatePostRequest;
import com.dali.trip.vo.PostVO;

/**
 * 帖子服务接口
 */
public interface PostService {

    /**
     * 获取帖子列表
     */
    PageResult<PostVO> getPostList(String sort, Integer page, Integer size, Long currentUserId);

    /**
     * 获取帖子详情
     */
    PostVO getPostDetail(Long id, Long currentUserId);

    /**
     * 发布帖子
     */
    Long createPost(CreatePostRequest request, Long userId);

    /**
     * 点赞帖子
     */
    PostVO likePost(Long postId, Long userId);

    /**
     * 取消点赞
     */
    PostVO unlikePost(Long postId, Long userId);

    /**
     * 收藏帖子
     */
    PostVO favoritePost(Long postId, Long userId);

    /**
     * 取消收藏
     */
    PostVO unfavoritePost(Long postId, Long userId);

    /**
     * 获取我发布的帖子
     */
    PageResult<PostVO> getMyPosts(Long userId, Integer page, Integer size);

    /**
     * 获取我收藏的帖子
     */
    PageResult<PostVO> getMyFavorites(Long userId, Integer page, Integer size);
}
