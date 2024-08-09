package com.sparta.elevenbookshelf.domain.post.dto;

import com.sparta.elevenbookshelf.domain.post.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostResponseDto {

    private Long id;
    private Long userId;
    private String nickname;
    private String postType;
    private int viewCount;
    private Long contentId;
    private Double rating;
    private String title;
    private List<String> tags = new ArrayList<>();
    private String body;
    private LocalDateTime createdAt;
    private  LocalDateTime modifiedAt;


    public PostResponseDto (Post post) {

        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.nickname = post.getUser().getNickname();
        this.postType = post.getType().toString();
        this.viewCount = post.getViewCount();
        this.contentId = post.getContent().getId();
        this.rating = post.getRating();
        this.title = post.getTitle();
        this.tags = post.getPostHashtags().stream()
                .map(postHashtag -> postHashtag.getHashtag().getTag())
                .toList();
        this.body = post.getBody();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}
