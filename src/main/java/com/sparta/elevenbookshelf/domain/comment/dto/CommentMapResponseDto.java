package com.sparta.elevenbookshelf.domain.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CommentMapResponseDto {

    private int totalPages;
    private List<CommentResponseDto> responseDtoList;

    public CommentMapResponseDto(int totalPages, List<CommentResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }
}
