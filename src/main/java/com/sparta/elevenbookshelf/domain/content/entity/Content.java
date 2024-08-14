package com.sparta.elevenbookshelf.domain.content.entity;

import com.sparta.elevenbookshelf.common.Timestamp;
import com.sparta.elevenbookshelf.domain.content.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    private String title;

    private String imgUrl;

    @Column(length = 1000)
    private String description;

    private String author;

    private String platform;

    @Column(length = 1000)
    private String url;

    private String genre;

    private String contentHashTag;

    private Double view;

    private Double rating;

    private Long bookMarkCount;

    private Long likeCount;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Enumerated(EnumType.STRING)
    private ContentEnd isEnd;

    @OneToMany(mappedBy = "content")
    private List<Post> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private Set<ContentHashtag> contentHashtags = new HashSet<>();


    @Builder
    public Content(String title, String imgUrl, String description, String author, String platform, String url, String genre, String contentHashTag,
                   Double view, Double rating, Long bookMarkCount, Long likeCount, ContentType type, ContentEnd isEnd) {

        this.title = title;
        this.imgUrl = imgUrl;
        this.description = description;
        this.author = author;
        this.platform = platform;
        this.url = url;
        this.genre = genre;
        this.contentHashTag = contentHashTag;
        this.view = view;
        this.rating = rating;
        this.type = type;
        this.isEnd = isEnd;
        this.bookMarkCount = bookMarkCount;
        this.likeCount = likeCount;
    }

    public Content (ContentRequestDto req) {
        this.title = req.getTitle();
        this.imgUrl = req.getImgUrl();
        this.description = req.getDescription();
        this.author = req.getAuthor();
        this.platform = req.getPlatform();
        this.url = req.getUrl();
        this.genre = req.getGenre();
        this.contentHashTag = req.getContentHashTag();
        this.view = req.getView();
        this.rating = req.getRating();
        this.type = req.getType();
        this.isEnd = req.getIsEnd();
        this.bookMarkCount = req.getBookMarkCount();
        this.likeCount = req.getLikeCount();
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public void addReview (Post review) {
        this.reviews.add(review);
    }

    public void incrementView() {
        this.view ++;
    }

    public void addBockMarkCount(){
        this.bookMarkCount++;
    }

    public void addHashtags (Set<ContentHashtag> contentHashtags) {

        this.contentHashtags.addAll(contentHashtags);
    }

    public void updateContent(ContentRequestDto requestDto) {

        this.title = requestDto.getTitle();
        this.imgUrl = requestDto.getImgUrl();
        this.description = requestDto.getDescription();
        this.author = requestDto.getAuthor();
        this.platform = requestDto.getPlatform();
        this.url = requestDto.getUrl();
        this.genre = requestDto.getGenre();
        this.contentHashTag = requestDto.getContentHashTag();
        this.view = requestDto.getView();
        this.rating = requestDto.getRating();
        this.bookMarkCount = requestDto.getBookMarkCount();
        this.likeCount = requestDto.getLikeCount();
        this.type = requestDto.getType();
        this.isEnd = requestDto.getIsEnd();
    }

    //:::::::::::::::::// enum //::::::::::::::::://

    @Getter
    @RequiredArgsConstructor
    public enum ContentType {
        COMICS,
        NOVEL
    }

    @Getter
    @RequiredArgsConstructor
    public enum ContentEnd {
        END,
        NOT
    }
}
