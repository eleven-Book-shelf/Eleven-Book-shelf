package com.sparta.elevenbookshelf.dto;

import lombok.Data;

@Data
public class PostRequestDto {

    private String postType;
    private Long boardId;
    private String title;
    private String body;
    private Long contentId;
    private Double rating;
}
