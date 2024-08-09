package com.sparta.elevenbookshelf.domain.post.repository;

import com.sparta.elevenbookshelf.domain.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getReviewPosts(long offset, int pagesize);

    List<Post> getPostsByContentId(Long contentId, long offset, int pagesize);

    List<Post> getPostsByUserId(Long userId, long offset, int pageSize);

    List<Post> findReviewsByHashtagContainKeyword(String keyword, long offset, int pagesize);
}


