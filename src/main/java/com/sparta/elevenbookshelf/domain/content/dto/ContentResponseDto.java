package com.sparta.elevenbookshelf.domain.content.dto;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import lombok.Data;

@Data
public class ContentResponseDto {

    private Long id;
    private String title;
    private String imgUrl;
    private String description;
    private String author;
    private String platform;
    private String url;
    private String genre;
    private String contentHashTag;
    private Double view;
    private Double rating;
    private Long bookMarkCount;
    private Long likeCount;
    private Content.ContentType type;
    private Content.ContentEnd isEnd;

    public ContentResponseDto(Content content) {

        this.id = content.getId();
        this.title = content.getTitle();
        this.imgUrl = content.getImgUrl();
        this.description = content.getDescription();
        this.author = content.getAuthor();
        this.platform = content.getPlatform();
        this.url = content.getUrl();
        this.genre = content.getGenre();
        this.contentHashTag = content.getContentHashTag();
        this.view = content.getView();
        this.rating = content.getRating();
        this.bookMarkCount = content.getBookMarkCount();
        this.likeCount = content.getLikeCount();
        this.type = content.getType();
        this.isEnd = content.getIsEnd();

    }
}
