package com.sparta.elevenbookshelf.domain.bookMark.repository;

import com.sparta.elevenbookshelf.domain.bookMark.entity.BookMark;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepositoryCustom {
    Optional<BookMark> findByUserAndPost(Long userId, Long postId);
    List<BookMark> findAllByUser(Long userId, Long offset, int pageSize);
    void deleteByUserAndPost(Long userId, Long postId);
}
