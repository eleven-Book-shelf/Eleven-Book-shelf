package com.sparta.elevenbookshelf.domain.hashtag.dto;

import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import lombok.Data;

@Data
public class HashtagResponseDto {

    private Long id;
    private double tier;
    private String tag;
    private int count;

    public HashtagResponseDto(Hashtag hashtag) {

        this.id = hashtag.getId();
        this.tier = hashtag.getTier();
        this.tag = "#" + hashtag.getTag();
        this.count = hashtag.getCount();

    }
}
