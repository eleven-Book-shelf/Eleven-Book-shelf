package com.sparta.elevenbookshelf.domain.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.like.entity.LikeComment;

import com.sparta.elevenbookshelf.domain.like.entity.QLikeComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeCommentRepositoryCustomImpl implements LikeCommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId) {
        QLikeComment likeComment = QLikeComment.likeComment;

        LikeComment foundLikeComment = jpaQueryFactory.selectFrom(likeComment)
                .where(likeComment.user.id.eq(userId)
                        .and(likeComment.comment.id.eq(commentId)))
                .fetchOne();

        return Optional.ofNullable(foundLikeComment);
    }
}