package com.sparta.elevenbookshelf.dto;

import lombok.Data;

@Data
public class PostRequestDto {

    private Long boardId;
    private String title;
    private String body;


}
