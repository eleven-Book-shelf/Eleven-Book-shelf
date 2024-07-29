package com.sparta.elevenbookshelf.repository;

import com.sparta.elevenbookshelf.entity.BookMark;

import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    Optional<BookMark> findByUserAndPost(User user, Post Post);
    List<BookMark> findAllByUser(User user);
    void deleteByUserAndPost(User user, Post Post);
}
