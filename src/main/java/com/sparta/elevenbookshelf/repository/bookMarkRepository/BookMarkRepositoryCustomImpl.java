package com.sparta.elevenbookshelf.repository.bookMarkRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookMarkRepositoryCustomImpl implements BookMarkRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
  /*
    @Override
    public Optional<List<Content>> findAllByUserIdAndBookMark(Long postId, long offset, int pageSize) {
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, comment.id);

        List<Content> content = jpaQueryFactory.selectFrom()
                .leftJoin(comment.children).fetchJoin()
                .where(comment.post.id.eq(postId).and(comment.parent.isNull()))
                .offset(offset)
                .limit(pageSize)
                .orderBy(orderSpecifier)
                .fetch();

        return Optional.ofNullable(content);
    }*/
}
