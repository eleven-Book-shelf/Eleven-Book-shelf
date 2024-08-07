package com.sparta.elevenbookshelf.domain.board.dto;

import com.sparta.elevenbookshelf.domain.board.entity.Board;
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
