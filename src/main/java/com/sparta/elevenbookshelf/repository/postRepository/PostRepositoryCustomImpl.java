package com.sparta.elevenbookshelf.repository.postRepository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.entity.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.elevenbookshelf.entity.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getPostsByBoard(Long boardId, long offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .where(post.board.id.eq(boardId))
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }

    @Transactional(readOnly = true)
    public Long getTotalPostsByBoard(Long boardId) {
        return jpaQueryFactory.selectFrom(post)
                .where(post.board.id.eq(boardId))
                .fetchCount();
    }

    @Override
    public List<Post> getPostsByContent(Long contentId, long offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .where(post.content.id.eq(contentId))
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }
}