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
    private Double view;
    private Double rating;
    private Content.ContentType type;
    private Content.ContentEnd isEnd;

}
