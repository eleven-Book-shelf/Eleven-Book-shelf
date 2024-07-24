package com.sparta.elevenbookshelf.repository.commentRepository;

import com.sparta.elevenbookshelf.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment>, CommentRepositoryCustom{


}
