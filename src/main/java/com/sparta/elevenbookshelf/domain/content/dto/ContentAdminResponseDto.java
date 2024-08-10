package com.sparta.elevenbookshelf.domain.content.dto;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import lombok.Data;

@Data
public class ContentAdminResponseDto {

    private Long id;
    private String title;
    private String description;
    private String author;
    private String platform;
    private String genre;
    private Double view;
    private Double rating;
    private Long bookMarkCount;
    private Long likeCount;
    private Content.ContentType type;
    private Content.ContentEnd isEnd;

    public ContentAdminResponseDto(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.description = content.getDescription();
        this.author = content.getAuthor();
        this.platform = content.getPlatform();
        this.genre = content.getGenre();
        this.view = content.getView();
        this.rating = content.getRating();
        this.bookMarkCount = content.getBookMarkCount();
        this.likeCount = content.getLikeCount();
        this.type = content.getType();
        this.isEnd = content.getIsEnd();

    }
}
