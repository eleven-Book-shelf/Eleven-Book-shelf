package com.sparta.elevenbookshelf.dto;

import lombok.Data;

@Data
public class PostRequestDto {

    private String postType;
    private Long boardId;
    private String title;
    private String body;
    private String hashtag;
    private Long contentId;
    private String prehashtag;
    private Double rating;

    public PostRequestDto(ContentResponseDto res) {
        this.postType = "CONTENT";
        this.boardId = 1L;
        this.contentId = res.getId();
    }
}
