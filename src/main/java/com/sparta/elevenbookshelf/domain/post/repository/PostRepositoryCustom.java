package com.sparta.elevenbookshelf.domain.post.repository;

import com.sparta.elevenbookshelf.domain.post.dto.PostSearchCond;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getPostsBySearchCondition(long offset, int pagesize, PostSearchCond cond);

    List<Post> getPosts(long offset, int pagesize);

    List<Post> getReviewPosts(long offset, int pagesize);

    List<Post> getPostsByContentId(Long contentId, long offset, int pagesize);

    Page<Post> getPostsByUserId(Long userId, int page, int pageSize, boolean asc);

    List<Post> findReviewsByHashtagContainKeyword(String keyword, long offset, int pagesize);

    Page<Post> findReviewsByHashtagContainPostType(Post.PostType type, int page, int pageSize, boolean asc);
}


