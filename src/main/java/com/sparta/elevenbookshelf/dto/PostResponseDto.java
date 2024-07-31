package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.post.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDto {

    private Long id;
    private Long userId;
    private String postType;
    private String title;
    private String body;
    private String nickname;
    private Long boardId;
    private LocalDateTime createdAt;
    private int viewCount;

    public PostResponseDto(Post post) {

        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.postType = post.getPostType();
        this.viewCount = post.getViewCount();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.nickname = post.getUser().getNickname();
        this.boardId = post.getBoard().getId();
        this.createdAt = post.getCreatedAt();
    }
}
