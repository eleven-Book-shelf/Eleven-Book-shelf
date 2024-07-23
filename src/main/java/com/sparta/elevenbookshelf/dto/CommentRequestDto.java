package com.sparta.elevenbookshelf.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequestDto {
    private Long parentId;
    private String contents;
}
