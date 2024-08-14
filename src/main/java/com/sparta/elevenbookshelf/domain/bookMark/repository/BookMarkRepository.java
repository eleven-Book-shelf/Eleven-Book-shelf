package com.sparta.elevenbookshelf.domain.bookMark.repository;

import com.sparta.elevenbookshelf.domain.bookMark.entity.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long>,
        QuerydslPredicateExecutor<BookMark>
        ,BookMarkRepositoryCustom{

    Optional<BookMark> findByUserIdAndContentId(Long userId, Long contentId);
    boolean existsByUserIdAndContentId(Long userId, Long contentId);
}
