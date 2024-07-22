package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Comment;
import lombok.Data;

@Data
public class CommentResponseDto {

    private Long userId;
    private Long boardId;
    private String contents;

    public CommentResponseDto(Comment comment) {
        this.userId = comment.getUser().getId();
        this.boardId = comment.getBoard().getId();
        this.contents = comment.getContents();
    }
}
