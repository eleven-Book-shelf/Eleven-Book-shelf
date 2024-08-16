package com.sparta.elevenbookshelf.domain.comment.repository;

import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Page<Comment> findAllByPostIdAndParentIsNull(Long postId,  int page, int pageSize);

}
