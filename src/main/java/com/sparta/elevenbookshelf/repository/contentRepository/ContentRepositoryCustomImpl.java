package com.sparta.elevenbookshelf.repository.contentRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.entity.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.elevenbookshelf.entity.QContent.content;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Content> findTop50ByView() {
        return jpaQueryFactory.selectFrom(content)
                .orderBy(content.view.desc())
                .limit(50)
                .fetch();
    }

}
