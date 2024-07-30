package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Content;
import lombok.Data;

@Data
public class ContentDataResponseDto {

    private Long id;
    private String title;
    private String imgUrl;
    private String description;
    private String author;
    private String platform;
    private String url;
    private String genre;
    private Double view;
    private Double rating;
    private Long bookMark;
    private Long likeCount;
    private Content.ContentType type;
    private Content.ContentEnd isEnd;

    public ContentDataResponseDto(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.imgUrl = content.getImgUrl();
        this.description = content.getDescription();
        this.author = content.getAuthor();
        this.platform = content.getPlatform();
        this.url = content.getUrl();
        this.genre = content.getGenre();
        this.view = content.getView();
        this.rating = content.getRating();
        this.bookMark = content.getBookMark();
        this.likeCount = content.getLikeCount();
        this.type = content.getType();
        this.isEnd = content.getIsEnd();

    }
}
