package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Board;
import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookMarkResponseDto {
    private Long id;
    private String title;
    private String content;

    public static BookMarkResponseDto fromPost(Post post) {
        return new BookMarkResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContents()
        );
    }
}
