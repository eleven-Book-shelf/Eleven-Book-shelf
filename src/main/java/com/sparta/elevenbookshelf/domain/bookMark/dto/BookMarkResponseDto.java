package com.sparta.elevenbookshelf.domain.bookMark.dto;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookMarkResponseDto {
    private Long id;
    private String title;
    private String content;

    public static BookMarkResponseDto fromPost(Content content) {
        return new BookMarkResponseDto(
                content.getId(),
                content.getTitle(),
                content.getUrl()
        );
    }
}
