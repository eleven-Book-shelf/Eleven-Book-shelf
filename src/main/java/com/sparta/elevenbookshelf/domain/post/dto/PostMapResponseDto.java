package com.sparta.elevenbookshelf.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostMapResponseDto {

    private int totalPages;
    private List<PostResponseListDto> responseDtoList;

    public PostMapResponseDto(int totalPages, List<PostResponseListDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }
}
