package com.dali.trip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dali.trip.common.BusinessException;
import com.dali.trip.common.PageResult;
import com.dali.trip.dto.CreatePostRequest;
import com.dali.trip.entity.*;
import com.dali.trip.mapper.*;
import com.dali.trip.service.PostService;
import com.dali.trip.vo.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final ItineraryMapper itineraryMapper;
    private final LikeMapper likeMapper;
    private final FavoriteMapper favoriteMapper;

    @Override
    public PageResult<PostVO> getPostList(String sort, Integer page, Integer size, Long currentUserId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1);

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Post::getLikeCount);
        } else {
            wrapper.orderByDesc(Post::getCreatedAt);
        }

        Page<Post> pageResult = postMapper.selectPage(new Page<>(page, size), wrapper);

        List<PostVO> voList = pageResult.getRecords().stream()
                .map(post -> toPostVO(post, currentUserId))
                .collect(Collectors.toList());

        return PageResult.of(voList, pageResult.getTotal(), page, size);
    }

    @Override
    public PostVO getPostDetail(Long id, Long currentUserId) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getStatus() == 0) {
            throw BusinessException.notFound("帖子不存在");
        }
        return toPostVO(post, currentUserId);
    }

    @Override
    @Transactional
    public Long createPost(CreatePostRequest request, Long userId) {
        // 检查行程是否存在
        Itinerary itinerary = itineraryMapper.selectById(request.getItineraryId());
        if (itinerary == null) {
            throw BusinessException.notFound("行程不存在");
        }

        // 检查是否有权限发布（必须是自己的行程）
        if (!userId.equals(itinerary.getUserId())) {
            throw BusinessException.forbidden("无权发布该行程");
        }

        // 检查是否已发布过
        Long count = postMapper.selectCount(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getItineraryId, request.getItineraryId())
                        .eq(Post::getStatus, 1)
        );
        if (count > 0) {
            throw BusinessException.badRequest("该行程已发布过帖子");
        }

        Post post = new Post();
        post.setAuthorId(userId);
        post.setItineraryId(request.getItineraryId());
        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setStatus(1);

        postMapper.insert(post);

        return post.getId();
    }

    @Override
    @Transactional
    public PostVO likePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) {
            throw BusinessException.notFound("帖子不存在");
        }

        // 检查是否已点赞
        Long count = likeMapper.selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getPostId, postId)
        );

        if (count == 0) {
            Like like = new Like();
            like.setUserId(userId);
            like.setPostId(postId);
            likeMapper.insert(like);

            post.setLikeCount(post.getLikeCount() + 1);
            postMapper.updateById(post);
        }

        PostVO vo = new PostVO();
        vo.setLiked(true);
        vo.setLikeCount(post.getLikeCount());
        return vo;
    }

    @Override
    @Transactional
    public PostVO unlikePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) {
            throw BusinessException.notFound("帖子不存在");
        }

        int deleted = likeMapper.delete(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getPostId, postId)
        );

        if (deleted > 0 && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postMapper.updateById(post);
        }

        PostVO vo = new PostVO();
        vo.setLiked(false);
        vo.setLikeCount(post.getLikeCount());
        return vo;
    }

    @Override
    @Transactional
    public PostVO favoritePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) {
            throw BusinessException.notFound("帖子不存在");
        }

        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getPostId, postId)
        );

        if (count == 0) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setPostId(postId);
            favoriteMapper.insert(favorite);

            post.setFavoriteCount(post.getFavoriteCount() + 1);
            postMapper.updateById(post);
        }

        PostVO vo = new PostVO();
        vo.setFavorited(true);
        vo.setFavoriteCount(post.getFavoriteCount());
        return vo;
    }

    @Override
    @Transactional
    public PostVO unfavoritePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) {
            throw BusinessException.notFound("帖子不存在");
        }

        int deleted = favoriteMapper.delete(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getPostId, postId)
        );

        if (deleted > 0 && post.getFavoriteCount() > 0) {
            post.setFavoriteCount(post.getFavoriteCount() - 1);
            postMapper.updateById(post);
        }

        PostVO vo = new PostVO();
        vo.setFavorited(false);
        vo.setFavoriteCount(post.getFavoriteCount());
        return vo;
    }

    @Override
    public PageResult<PostVO> getMyPosts(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getAuthorId, userId)
               .eq(Post::getStatus, 1)
               .orderByDesc(Post::getCreatedAt);

        Page<Post> pageResult = postMapper.selectPage(new Page<>(page, size), wrapper);

        List<PostVO> voList = pageResult.getRecords().stream()
                .map(post -> toPostVO(post, userId))
                .collect(Collectors.toList());

        return PageResult.of(voList, pageResult.getTotal(), page, size);
    }

    @Override
    public PageResult<PostVO> getMyFavorites(Long userId, Integer page, Integer size) {
        // 查询收藏的帖子ID
        Page<Favorite> favPage = favoriteMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt)
        );

        List<Long> postIds = favPage.getRecords().stream()
                .map(Favorite::getPostId)
                .collect(Collectors.toList());

        if (postIds.isEmpty()) {
            return PageResult.of(List.of(), 0L, page, size);
        }

        List<Post> posts = postMapper.selectBatchIds(postIds);
        List<PostVO> voList = posts.stream()
                .filter(p -> p.getStatus() == 1)
                .map(post -> toPostVO(post, userId))
                .collect(Collectors.toList());

        return PageResult.of(voList, favPage.getTotal(), page, size);
    }

    private PostVO toPostVO(Post post, Long currentUserId) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setSummary(post.getSummary());
        vo.setLikeCount(post.getLikeCount());
        vo.setFavoriteCount(post.getFavoriteCount());
        vo.setCreatedAt(post.getCreatedAt());

        // 作者信息
        User author = userMapper.selectById(post.getAuthorId());
        if (author != null) {
            PostVO.AuthorVO authorVO = new PostVO.AuthorVO();
            authorVO.setId(author.getId());
            authorVO.setUsername(author.getUsername());
            authorVO.setAvatar(author.getAvatar());
            vo.setAuthor(authorVO);
        }

        // 行程信息
        Itinerary itinerary = itineraryMapper.selectById(post.getItineraryId());
        if (itinerary != null) {
            PostVO.ItinerarySummaryVO itineraryVO = new PostVO.ItinerarySummaryVO();
            itineraryVO.setId(itinerary.getId());
            itineraryVO.setCity(itinerary.getCity());
            itineraryVO.setDays(itinerary.getDays());
            itineraryVO.setTheme(itinerary.getTheme());
            vo.setItinerary(itineraryVO);
        }

        // 当前用户点赞/收藏状态
        if (currentUserId != null) {
            vo.setLiked(likeMapper.selectCount(
                    new LambdaQueryWrapper<Like>()
                            .eq(Like::getUserId, currentUserId)
                            .eq(Like::getPostId, post.getId())
            ) > 0);

            vo.setFavorited(favoriteMapper.selectCount(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, currentUserId)
                            .eq(Favorite::getPostId, post.getId())
            ) > 0);
        } else {
            vo.setLiked(false);
            vo.setFavorited(false);
        }

        return vo;
    }
}
