package com.sparta.elevenbookshelf.domain.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequestDto {
    private Long parentId;
    private String contents;
}
