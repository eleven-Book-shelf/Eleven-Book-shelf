package com.sparta.elevenbookshelf.domain.content.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.elevenbookshelf.domain.bookMark.entity.QBookMark.bookMark;
import static com.sparta.elevenbookshelf.domain.content.entity.QContent.content;
import static com.sparta.elevenbookshelf.domain.hashtag.entity.QHashtag.hashtag;
import static com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.QContentHashtag.contentHashtag;


@Repository
@RequiredArgsConstructor
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    // 검색할 장르 및 타입 설정
    private BooleanBuilder buildContentCondition(String genre, String contentType) {

        BooleanBuilder builder = new BooleanBuilder();

        if (contentType != null) {
            Content.ContentType type;

            if (contentType.equals(Content.ContentType.NOVEL.toString())) {
                type = Content.ContentType.NOVEL;
            } else {
                type = Content.ContentType.COMICS;
            }

            builder.and(content.type.eq(type));
        }

        if (genre != null && !genre.isEmpty()) {
            builder.and(content.contentHashTag.like("%" + genre + "%"));
        }

        return builder;
    }

    // 사용자 설정
    private BooleanBuilder buildUserCondition(Long userId) {

        BooleanBuilder builder = new BooleanBuilder();

        if (userId != null) {
            builder.and(bookMark.user.id.eq(userId));
        }

        return builder;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {

        if ("like".equalsIgnoreCase(sortBy)) {
            return content.likeCount.desc();
        }
        // 다른 정렬 기준 추가 가능
        return content.view.desc();// 기본 정렬 기준 : 좋아요 수
    }

    @Override
    public List<Content> findContentsBySearchCondition(long offset, int pagesize, Long userId, String genre, String contentType, String sortBy) {

        BooleanBuilder searchCond = new BooleanBuilder();
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortBy);

        searchCond.and(buildContentCondition(genre, contentType));

        if (userId != null) {

            searchCond.and(buildUserCondition(userId));
            return jpaQueryFactory.selectFrom(content)
                    .join(bookMark).on(content.id.eq(bookMark.content.id))
                    .where(searchCond)
                    .orderBy(orderSpecifier)
                    .offset(offset)
                    .limit(pagesize)
                    .fetch();
        } else {

            return jpaQueryFactory.selectFrom(content)
                    .where(searchCond)
                    .orderBy(orderSpecifier)
                    .offset(offset)
                    .limit(pagesize)
                    .fetch();
        }


    }


    @Override
    public List<Content> findContentsByGenre(long offset, int pageSize, String platform, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .orderBy(content.view.desc())
                .where((genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null),
                        (platform != null && !platform.isEmpty() ? content.platform.eq(platform) : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> findTopByView(long offset, int pageSize, Content.ContentType contentType, String genre) {
        return jpaQueryFactory.selectFrom(content)
                .orderBy(content.view.desc())
                .where(content.type.eq(contentType)
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> findWebtoonContentsByGenre(long offset, int pageSize, String platform, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .where(content.type.eq(Content.ContentType.COMICS)
                               .and(platform != null && !platform.isEmpty() ? content.platform.eq(platform) : null)
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> findWebnovelContentsByGenre(long offset, int pageSize, String platform, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .where(content.type.eq(Content.ContentType.NOVEL)
                               .and(platform != null && !platform.isEmpty() ? content.platform.eq(platform ) : null)
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> findWebtoonContentsByGenreByUser(Long userId, long offset, int pageSize, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .join(bookMark).on(content.id.eq(bookMark.content.id))
                .where(bookMark.user.id.eq(userId)
                               .and(content.type.eq(Content.ContentType.COMICS))
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> findWebnovelContentsByGenreByUser(Long userId, long offset, int pageSize, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .join(bookMark).on(content.id.eq(bookMark.content.id))
                .where(bookMark.user.id.eq(userId)
                               .and(content.type.eq(Content.ContentType.NOVEL))
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> findContentsByHashtagContainKeyword(String keyword, long offset, int pageSize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, content.likeCount);

        return jpaQueryFactory.selectFrom(content)
                .join(content.contentHashtags, contentHashtag)
                .join(contentHashtag.hashtag, hashtag)
                .where(hashtag.tag.contains(keyword))
                .offset(offset)
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();
    }

    @Override
    public Page<Content> findreadSearchByKeyword(String keyword, int offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, content.createdAt);

        Pageable pageable = PageRequest.of(offset, pagesize);

        List<Content> contents = jpaQueryFactory.selectFrom(content)
                .where(content.title.like("%" + keyword + "%"))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        long total = jpaQueryFactory.selectFrom(content)
                .where(content.title.like("%" + keyword + "%"))
                .fetchCount();

        return new PageImpl<>(contents, pageable, total);
    }

}
