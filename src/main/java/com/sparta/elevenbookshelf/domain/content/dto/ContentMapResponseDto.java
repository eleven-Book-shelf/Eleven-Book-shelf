package com.sparta.elevenbookshelf.domain.content.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ContentMapResponseDto {

    private int totalPages;
    private List<ContentResponseDto> responseDtoList;

    public ContentMapResponseDto(int totalPages, List<ContentResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }

}
