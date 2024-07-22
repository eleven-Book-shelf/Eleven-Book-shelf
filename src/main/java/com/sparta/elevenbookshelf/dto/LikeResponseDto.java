package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.LikeBoard;
import com.sparta.elevenbookshelf.entity.LikeComment;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class LikeResponseDto {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public LikeResponseDto(LikeBoard like) {
        id = like.getBoard().getId();
        createdAt = like.getCreatedAt();
        modifiedAt = like.getModifiedAt();
    }

    public LikeResponseDto(LikeComment like) {
        id = like.getComment().getId();
        createdAt = like.getCreatedAt();
        modifiedAt = like.getModifiedAt();
    }
}
