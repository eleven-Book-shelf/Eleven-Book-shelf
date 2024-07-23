package com.sparta.elevenbookshelf.repository;

import com.sparta.elevenbookshelf.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByBoardIdAndParentIsNull(Long boardId);
}
