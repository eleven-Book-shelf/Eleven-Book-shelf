package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Hashtag;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserHashtagResponseDto {

    Long id;
    String tag;

    public UserHashtagResponseDto(Hashtag hashtag) {
        this.id = hashtag.getId();
        this.tag = "#" + hashtag.getTag();

    }
}
