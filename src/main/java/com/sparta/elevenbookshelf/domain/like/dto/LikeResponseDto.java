package com.sparta.elevenbookshelf.domain.like.dto;

import com.sparta.elevenbookshelf.domain.like.entity.LikeComment;
import com.sparta.elevenbookshelf.domain.like.entity.LikePost;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeResponseDto {

    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public LikeResponseDto(LikePost like) {
        id = like.getPost().getId();
        createdAt = like.getCreatedAt();
        modifiedAt = like.getModifiedAt();
    }

    public LikeResponseDto(LikeComment like) {
        id = like.getComment().getId();
        createdAt = like.getCreatedAt();
        modifiedAt = like.getModifiedAt();
    }
}
