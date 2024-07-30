package com.sparta.elevenbookshelf.repository.likeRepository;

import com.sparta.elevenbookshelf.entity.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Long>,
        QuerydslPredicateExecutor<LikeComment>,
        LikeCommentRepositoryCustom {

    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);

    Integer countByCommentId(Long id);

    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);
}
