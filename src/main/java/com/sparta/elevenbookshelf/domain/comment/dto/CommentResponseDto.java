package com.sparta.elevenbookshelf.domain.comment.dto;

import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long id;
    private Long userId;
    private String nickname;
    private Long postId;
    private int likes;
    private String contents;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> children = new ArrayList<>();

    public CommentResponseDto(Comment comment) {
        this.nickname = comment.getUser().getNickname();
        this.postId = comment.getPost().getId();
        this.contents = comment.getContents();
        for (Comment child : comment.getChildren()) {
            this.children.add(new CommentResponseDto(child));
        }
    }

    public CommentResponseDto(Long id, Long userId, Long postId, String nickname, String contents, List<CommentResponseDto> childrenDto,int likes ,LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.postId = postId;
        this.createdAt = createdAt;
        this.likes = likes;
        this.contents = contents;
        this.children = childrenDto;
    }

}
