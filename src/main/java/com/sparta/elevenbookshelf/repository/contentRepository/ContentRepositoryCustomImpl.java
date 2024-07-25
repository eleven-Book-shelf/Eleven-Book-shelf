package com.sparta.elevenbookshelf.repository.contentRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

}
