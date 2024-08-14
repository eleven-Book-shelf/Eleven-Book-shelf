package com.sparta.elevenbookshelf.domain.post.dto;

import com.sparta.elevenbookshelf.domain.post.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseListDto {

    private Long id;
    private Long userId;
    private String nickname;
    private String title;
    private String postType;
    private int viewCount;
    private Long contentId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostResponseListDto(Post post) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.title = post.getTitle();
        this.nickname = post.getUser().getNickname();
        this.postType = post.getType().toString();
        this.viewCount = post.getViewCount();
        this.contentId = (post.getContent() != null) ? post.getContent().getId() : 0L;
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}
