package com.sparta.elevenbookshelf.domain.hashtag.dto;

import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import lombok.Data;

@Data
public class HashtagResponseDto {

    private Long id;
    private String tag;

    public HashtagResponseDto(Hashtag hashtag) {

        this.id = hashtag.getId();
        this.tag = "#" + hashtag.getTag();
    }
}
