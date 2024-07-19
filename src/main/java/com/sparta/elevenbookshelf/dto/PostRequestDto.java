package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Post;
import lombok.Getter;

@Getter
public class PostRequestDto {

    private Post.PostType postType;
    private Long boardId;
    private String title;
    private String content;
}
