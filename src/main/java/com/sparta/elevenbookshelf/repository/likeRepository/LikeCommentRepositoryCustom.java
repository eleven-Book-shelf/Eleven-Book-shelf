package com.sparta.elevenbookshelf.repository.likeRepository;

import com.sparta.elevenbookshelf.entity.LikeComment;

import java.util.Optional;

public interface LikeCommentRepositoryCustom {
    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);
}
