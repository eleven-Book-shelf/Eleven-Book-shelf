package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Hashtag;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class UserHashtagResponseDto {

    List<String> hashtags;

    @Builder
    public UserHashtagResponseDto(List<Hashtag> hashtags) {

        this.hashtags = hashtags.stream().map(Hashtag::getTag).toList();
    }
}
