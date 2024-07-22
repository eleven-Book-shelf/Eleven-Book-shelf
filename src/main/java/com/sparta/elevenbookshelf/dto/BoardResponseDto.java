package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Board;
import lombok.Data;

@Data
public class BoardResponseDto {

    private Long id;
    private String title;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
    }
}
