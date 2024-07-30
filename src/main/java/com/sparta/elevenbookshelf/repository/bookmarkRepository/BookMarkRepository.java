package com.sparta.elevenbookshelf.repository.bookmarkRepository;

import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long>,
        QuerydslPredicateExecutor<BookMark>,
        BookMarkRepositoryCustom{

}
