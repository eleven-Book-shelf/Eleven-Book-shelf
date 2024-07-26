package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Post;
import lombok.Data;

@Data
public class PostRequestDto {

    private Post.PostType postType;
    private Long boardId;
    private String title;
    private String contents;
}
