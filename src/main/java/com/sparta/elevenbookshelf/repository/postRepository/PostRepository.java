package com.sparta.elevenbookshelf.repository.postRepository;

import com.sparta.elevenbookshelf.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface PostRepository extends JpaRepository<Post, Long>,
        QuerydslPredicateExecutor<Post>, PostRepositoryCustom {
}
