package com.sparta.elevenbookshelf.repository.postRepository;

import com.sparta.elevenbookshelf.entity.post.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getPostsByBoard(Long boardId, long offset, int pagesize);

    List<Post> getPostsByContent(Long contentId, long offset, int pagesize);
}
