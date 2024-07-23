package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.LikeComment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long userId;
    private Long boardId;
    private String contents;
    private List<CommentResponseDto> children = new ArrayList<>();

    public CommentResponseDto(Comment comment) {
        this.userId = comment.getUser().getId();
        this.boardId = comment.getBoard().getId();
        this.contents = comment.getContents();
        for (Comment child : comment.getChildren()) {
            this.children.add(new CommentResponseDto(child));
        }
    }


}
