package com.sparta.elevenbookshelf.domain.comment.dto;

import com.sparta.elevenbookshelf.domain.comment.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Optional<List<Comment>> findAllByPostIdAndParentIsNull(Long postId, long offset, int pagesize);
}
