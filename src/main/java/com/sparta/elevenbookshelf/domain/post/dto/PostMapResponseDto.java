package com.sparta.elevenbookshelf.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostMapResponseDto {

    private int totalPages;
    private List<PostResponseDto> responseDtoList;

    public PostMapResponseDto(int totalPages, List<PostResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }
}
