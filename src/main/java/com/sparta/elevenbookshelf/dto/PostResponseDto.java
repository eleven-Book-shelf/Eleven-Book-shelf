package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.post.Post;
import lombok.Data;

@Data
public class PostResponseDto {

    private Long id;
    private String title;
    private String body;
    private String username;
    private Long boardId;

    public PostResponseDto(Post post) {

        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.username = post.getUser().getUsername();
        this.boardId = post.getBoard().getId();
    }
}
