package com.sparta.elevenbookshelf.domain.post.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.elevenbookshelf.domain.hashtag.entity.QHashtag.hashtag;
import static com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.QPostHashtag.postHashtag;
import static com.sparta.elevenbookshelf.domain.post.entity.QPost.post;


@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getReviewPosts(long offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .where(post.type.eq(Post.PostType.REVIEW))
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }

    @Override
    public List<Post> getPostsByContentId(Long contentId, long offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .where(post.content.id.eq(contentId))
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }

    @Override
    public List<Post> getPostsByUserId(Long userId, long offset, int pageSize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .where(post.user.id.eq(userId))
                .offset(offset)
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();
    }

    @Override
    public List<Post> findReviewsByHashtagContainKeyword(String keyword, long offset, int pageSize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .join(post.postHashtags, postHashtag)
                .join(postHashtag.hashtag, hashtag)
                .where(hashtag.tag.contains(keyword))
                .offset(offset)
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();
    }
}