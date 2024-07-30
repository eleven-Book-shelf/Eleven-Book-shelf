package com.sparta.elevenbookshelf.repository;

import com.sparta.elevenbookshelf.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {

    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);

    Integer countByCommentId(Long id);

    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);
}
