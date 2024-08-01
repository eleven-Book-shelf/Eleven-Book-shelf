package com.sparta.elevenbookshelf.repository.likeCommentRepository;

import com.sparta.elevenbookshelf.entity.LikeComment;

import java.util.Optional;

public interface LikeCommentRepositoryCustom {
    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);
}
