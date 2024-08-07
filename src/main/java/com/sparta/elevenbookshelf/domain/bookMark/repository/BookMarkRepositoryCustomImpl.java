package com.sparta.elevenbookshelf.domain.bookMark.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.bookMark.entity.BookMark;
import com.sparta.elevenbookshelf.domain.bookMark.entity.QBookMark;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookMarkRepositoryCustomImpl implements BookMarkRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<BookMark> findByUserAndPost(Long userId, Long postId) {
        QBookMark bookMark = QBookMark.bookMark;

        BookMark foundBookMark = jpaQueryFactory.selectFrom(bookMark)
                .where(bookMark.user.id.eq(userId)
                        .and(bookMark.content.id.eq(postId)))
                .fetchOne();

        return Optional.ofNullable(foundBookMark);
    }

    @Override
    public List<BookMark> findAllByUser(Long userId, Long offset, int pageSize) {
        QBookMark bookMark = QBookMark.bookMark;

        return jpaQueryFactory.selectFrom(bookMark)
                .where(bookMark.user.id.eq(userId))
                .offset(offset)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public void deleteByUserAndPost(Long userId, Long postId) {
        QBookMark bookMark = QBookMark.bookMark;

        jpaQueryFactory.delete(bookMark)
                .where(bookMark.user.id.eq(userId)
                        .and(bookMark.content.id.eq(postId)))
                .execute();
    }
}
