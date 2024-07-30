package com.sparta.elevenbookshelf.repository.bookmarkRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.LikeComment;
import com.sparta.elevenbookshelf.entity.QBookMark;
import com.sparta.elevenbookshelf.entity.QLikeComment;
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
                        .and(bookMark.post.id.eq(postId)))
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
                        .and(bookMark.post.id.eq(postId)))
                .execute();
    }
}
