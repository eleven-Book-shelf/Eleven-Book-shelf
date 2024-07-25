package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.LikeComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long userId;
    private Long postId;
    private String contents;
    private List<CommentResponseDto> children = new ArrayList<>();

    public CommentResponseDto(Comment comment) {
        this.userId = comment.getUser().getId();
        this.postId = comment.getPost().getId();
        this.contents = comment.getContents();
        for (Comment child : comment.getChildren()) {
            this.children.add(new CommentResponseDto(child));
        }
    }


    public CommentResponseDto(Long userId, Long postId, String contents, List<CommentResponseDto> childrenDto) {
        this.userId = userId;
        this.postId = postId;
        this.contents = contents;
        this.children = childrenDto;
    }
}
