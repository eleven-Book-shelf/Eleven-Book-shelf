package com.sparta.elevenbookshelf.repository.bookmarkRepository;

import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.post.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepositoryCustom {
    Optional<BookMark> findByUserAndPost(Long userId, Long postId);
    List<BookMark> findAllByUser(Long userId, Long offset, int pageSize);
    void deleteByUserAndPost(Long userId, Long postId);
}
