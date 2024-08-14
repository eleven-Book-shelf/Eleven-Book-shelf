package com.sparta.elevenbookshelf.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.post.dto.PostSearchCond;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.elevenbookshelf.domain.content.entity.QContent.content;
import static com.sparta.elevenbookshelf.domain.hashtag.entity.QHashtag.hashtag;
import static com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.QPostHashtag.postHashtag;
import static com.sparta.elevenbookshelf.domain.post.entity.QPost.post;



@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {

        if ("view" .equalsIgnoreCase(sortBy)) {
            return post.viewCount.desc();
        }
        // 다른 정렬 기준 추가 가능
        return post.createdAt.desc();
    }

    private BooleanBuilder buildPostCondition(PostSearchCond cond) {

        BooleanBuilder builder = new BooleanBuilder();

        if (cond.getPostType() != null) {
            Post.PostType type;

            if (cond.getPostType().equals(Post.PostType.NORMAL.toString())) {
                type = Post.PostType.NORMAL;
            } else if (cond.getPostType().equals(Post.PostType.REVIEW.toString())){
                type = Post.PostType.REVIEW;
            } else {
                type = Post.PostType.NOTICE;
            }

            builder.and(post.type.eq(type));
        }

        if (cond.getKeyword() != null && !cond.getKeyword().isEmpty()) {
            builder.and(post.postHashtag.like("%" + cond.getKeyword() + "%"));
        }

        if (cond.getContentId() != null) {
            builder.and(post.content.id.eq(cond.getContentId()));
        }

        if (cond.getUserId() != null) {
            builder.and(post.user.id.eq(cond.getUserId()));
        }

        return builder;
    }

    @Override
    public List<Post> getPostsBySearchCondition(long offset, int pagesize, PostSearchCond cond) {

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(cond.getSortBy());
        BooleanBuilder searchCond = buildPostCondition(cond);

         return jpaQueryFactory.selectFrom(post)
                .where(searchCond)
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();

    }

    @Override
    public List<Post> getPosts(long offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, post.createdAt);

        return jpaQueryFactory.selectFrom(post)
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }

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

    @Override
    public Page<Post> findReviewsByHashtagContainPostType(Post.PostType type, int page, int pageSize, boolean asc) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(
                asc ? Order.ASC : Order.DESC,
                post.createdAt
        );

        long totalCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(post.count())
                        .from(post)
                        .where(post.type.eq(type))
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, pageSize);

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(post.type.eq(type))
                .offset(pageable.getOffset())
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();

        return new PageImpl<>(posts, pageable, totalCount);
    }


}