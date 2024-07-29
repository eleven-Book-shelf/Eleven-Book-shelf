package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Content;
import lombok.Data;

@Data
public class ContentRequestDto {

    private String title;
    private String imgUrl;
    private String description;
    private String author;
    private String platform;
    private String url;
    private String genre;
    private Double view;
    private Double rating;
    private Long bookMark;
    private Long likeCount;
    private Content.ContentType type;
    private Content.ContentEnd isEnd;

}
