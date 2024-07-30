package com.sparta.elevenbookshelf.repository.commentRepository;

import com.sparta.elevenbookshelf.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Optional<List<Comment>> findAllByPostIdAndParentIsNull(Long postId, long offset, int pagesize);
    Optional<List<Comment>> findAllByContentIdAndParentIsNull(Long contentId, long offset, int pagesize);
}
