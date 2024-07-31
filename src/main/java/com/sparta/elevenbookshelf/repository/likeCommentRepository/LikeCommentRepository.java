package com.sparta.elevenbookshelf.repository.likeCommentRepository;

import com.sparta.elevenbookshelf.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Long>,
        QuerydslPredicateExecutor<LikeComment>,
        LikeCommentRepositoryCustom {

    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);

    Integer countByCommentId(Long id);

    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);
}
