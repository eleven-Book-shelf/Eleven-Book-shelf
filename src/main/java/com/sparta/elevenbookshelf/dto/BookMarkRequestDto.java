package com.sparta.elevenbookshelf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookMarkRequestDto {
    private Long userId;
    private String message;
    private boolean status;
}
