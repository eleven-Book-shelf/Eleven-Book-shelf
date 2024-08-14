package com.sparta.elevenbookshelf.domain.like.repository;

import com.sparta.elevenbookshelf.domain.like.entity.LikeComment;

import java.util.Optional;

public interface LikeCommentRepositoryCustom {
    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);
}
