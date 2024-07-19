package com.sparta.elevenbookshelf.repository.postRepository;

import com.sparta.elevenbookshelf.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getPostsByBoard(Long boardId, long offset, int pagesize);
}
