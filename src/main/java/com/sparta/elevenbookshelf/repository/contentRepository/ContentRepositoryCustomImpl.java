package com.sparta.elevenbookshelf.repository.contentRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.entity.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.elevenbookshelf.entity.QBookMark.bookMark;
import static com.sparta.elevenbookshelf.entity.QContent.content;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Content> getContent(long offset, int pageSize, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .where(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null)
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> getContentByConic(long offset, int pageSize, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .where(content.type.eq(Content.ContentType.COMICS)
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> getContentByNovel(long offset, int pageSize, String genre) {
        return jpaQueryFactory
                .selectFrom(content)
                .where(content.type.eq(Content.ContentType.NOVEL)
                               .and(genre != null && !genre.isEmpty() ? content.contentHashTag.like("%" + genre + "%") : null))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Content> getContentByConicUser(Long userId, long offset, int pageSize, String genre) {
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
    public List<Content> getContentByNovelUser(Long userId, long offset, int pageSize, String genre) {
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
    public List<Content> search(int offset, int pagesize, String search) {
        return List.of();
    }
}
