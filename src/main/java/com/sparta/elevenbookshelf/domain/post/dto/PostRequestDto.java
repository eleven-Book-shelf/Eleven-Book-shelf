package com.sparta.elevenbookshelf.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostRequestDto {

    private String postType;
    private String title;
    private String body;
    private Long contentId;
    private String prehashtag;
    private Double rating;
}
