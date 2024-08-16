package com.sparta.elevenbookshelf.domain.content.dto;

import lombok.Data;

@Data
public class ContentSearchCond {

    private Long userId;
    private Boolean isBookmarked = false;
    private String keyword;
    private String contentType;
    private String sortBy;
}
