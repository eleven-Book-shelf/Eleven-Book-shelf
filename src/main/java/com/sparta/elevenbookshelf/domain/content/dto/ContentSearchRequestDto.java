package com.sparta.elevenbookshelf.domain.content.dto;

import lombok.Data;

@Data
public class ContentSearchRequestDto {

    private String isBookmarked;
    private String keyword;
    private String contentType;
    private String sortBy;
}
