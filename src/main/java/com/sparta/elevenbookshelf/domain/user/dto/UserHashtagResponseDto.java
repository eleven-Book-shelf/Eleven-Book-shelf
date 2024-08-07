package com.sparta.elevenbookshelf.domain.user.dto;

import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import lombok.Data;

@Data
public class UserHashtagResponseDto {

    Long id;
    String tag;

    public UserHashtagResponseDto(Hashtag hashtag) {

        this.id = hashtag.getId();
        this.tag = "#" + hashtag.getTag();
    }
}
