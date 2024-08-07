package com.sparta.elevenbookshelf.domain.post.dto;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
public class PostResponseDto {

    private Long id;
    private Long userId;
    private String postType;
    private String title;
    private String body;
    private String nickname;
    private Long boardId;
    private Long contentId;
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
        this.contentId = Optional.ofNullable(post.getContent()).map(Content::getId).orElse(null);
        this.createdAt = post.getCreatedAt();
    }
}
