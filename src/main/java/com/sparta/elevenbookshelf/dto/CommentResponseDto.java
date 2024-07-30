package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.LikeComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDto {

    private Long id;
    private Long userId;
    private String nickname;
    private Long postId;
    private Long contentId;
    private int likes;
    private String contents;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> children = new ArrayList<>();

    public CommentResponseDto(Comment comment) {
        this.nickname = comment.getUser().getNickname();
        this.postId = comment.getPost().getId();
        this.contentId = comment.getContent().getId();
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

    public CommentResponseDto(Long id, Long userId, String nickname, Long contentId, String contents,int likes, List<CommentResponseDto> childrenDto, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.contentId = contentId;
        this.createdAt = createdAt;
        this.likes = likes;
        this.contents = contents;
        this.children = childrenDto;
    }

}
