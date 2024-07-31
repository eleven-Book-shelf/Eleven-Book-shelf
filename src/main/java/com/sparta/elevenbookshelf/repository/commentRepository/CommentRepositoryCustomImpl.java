package com.sparta.elevenbookshelf.repository.commentRepository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.elevenbookshelf.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Optional<List<Comment>> findAllByPostIdAndParentIsNull(Long postId, long offset, int pageSize) {
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, comment.id);

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.children).fetchJoin()
                .where(comment.post.id.eq(postId).and(comment.parent.isNull()))
                .offset(offset)
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();

        return Optional.ofNullable(comments);
    }

}
