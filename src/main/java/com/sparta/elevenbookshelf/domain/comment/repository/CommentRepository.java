package com.sparta.elevenbookshelf.domain.comment.repository;

import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment>, CommentRepositoryCustom{

}
