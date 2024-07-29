package com.sparta.elevenbookshelf.dto;


import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookMarkResponseDto {
    private Long id;
    private String title;
    private Content content;

    public static BookMarkResponseDto fromPost(Post post) {
        return new BookMarkResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent()
        );
    }
}
