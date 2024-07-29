package com.sparta.elevenbookshelf.dto;


import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.post.Post;
import lombok.Data;

@Data
public class PostResponseDto {

    private Long id;
//    private Post.PostType postType;
    private String title;
    private Content content;
    private String username;
    private Long boardId;

    public PostResponseDto(Post post) {

        this.id = post.getId();
//        this.postType = post.getPostType();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUser().getUsername();
        this.boardId = post.getBoard().getId();
    }
}
