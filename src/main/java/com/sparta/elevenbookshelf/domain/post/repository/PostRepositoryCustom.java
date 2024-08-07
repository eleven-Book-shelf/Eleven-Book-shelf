package com.sparta.elevenbookshelf.domain.post.repository;

import com.sparta.elevenbookshelf.domain.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getPostsByBoard(Long boardId, long offset, int pagesize);

    List<Post> getPostsByContent(Long contentId, long offset, int pagesize);

    List<Post> getPostsByUserId(Long userId, long offset, int pageSize);

    Long getTotalPostsByBoard(Long boardId);

}


