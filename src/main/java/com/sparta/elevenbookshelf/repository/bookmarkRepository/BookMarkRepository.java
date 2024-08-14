package com.sparta.elevenbookshelf.repository.bookmarkRepository;

import com.sparta.elevenbookshelf.entity.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long>,
        QuerydslPredicateExecutor<BookMark>
        ,BookMarkRepositoryCustom{

    Optional<BookMark> findByUserIdAndContentId(Long userId, Long contentId);
    boolean existsByUserIdAndContentId(Long userId, Long contentId);
}
