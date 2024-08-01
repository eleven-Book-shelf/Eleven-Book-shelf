package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ContentResponseDto {

    private Long id;
    private String title;
    private String imgUrl;
    private String description;
    private String author;
    private List<String> hashtags = new ArrayList<>();
    private String platform;
    private String genre;
    private String contentHashTag;
    private Double view;
    private Double rating;
    private String url;
    private Content.ContentType type;
    private Content.ContentEnd isEnd;
    private List<PostResponseDto> posts = new ArrayList<>();

    public ContentResponseDto(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.imgUrl = content.getImgUrl();
        this.description = content.getDescription();
        this.author = content.getAuthor();
        this.hashtags.addAll(content.getContentHashtags().stream()
                .map(contentHashtag -> contentHashtag.getHashtag().getTag())
                .toList());
        this.platform = content.getPlatform();
        this.genre = content.getGenre();
        this.contentHashTag = content.getContentHashTag();
        this.view = content.getView();
        this.rating = content.getRating();
        this.url = content.getUrl();
        this.type = content.getType();
        this.isEnd = content.getIsEnd();
        this.posts.addAll(content.getReviews().stream()
                .map(PostResponseDto::new)
                .toList());
    }
}
