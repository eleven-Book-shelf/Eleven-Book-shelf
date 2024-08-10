package com.sparta.elevenbookshelf.domain.comment.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


import static com.sparta.elevenbookshelf.domain.comment.entity.QComment.comment;


@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Comment> findAllByPostIdAndParentIsNull(Long postId, int page, int pageSize) {
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.ASC, comment.id);

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(comment.count())
                        .from(comment)
                        .where(comment.post.id.eq(postId).and(comment.parent.isNull()))
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.children).fetchJoin()
                .where(comment.post.id.eq(postId).and(comment.parent.isNull()))
                .offset(pageable.getOffset())
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();

        return new PageImpl<>(comments, pageable, total);
    }

}
