package com.sparta.elevenbookshelf.entity;

import com.sparta.elevenbookshelf.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

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

    private Double view;

    private Double rating;

    private Long bookMarkCount;

    private Long likeCount;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Enumerated(EnumType.STRING)
    private ContentEnd isEnd;

    @OneToMany(mappedBy = "content", cascade = CascadeType.MERGE)
    private List<ReviewPost> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "content")
    private Set<ContentHashtag> contentHashtags = new HashSet<>();


    @Builder
    public Content(String title,
                   String imgUrl,
                   String description,
                   String author,
                   String platform,
                   String url,
                   String genre,
                   Double view,
                   Double rating,
                   Long bookMarkCount,
                   Long likeCount,
                   ContentType type,
                   ContentEnd isEnd

    ) {

        this.title = title;
        this.imgUrl = imgUrl;
        this.description = description;
        this.author = author;
        this.platform = platform;
        this.url = url;
        this.genre = genre;
        this.view = view;
        this.rating = rating;
        this.type = type;
        this.isEnd = isEnd;
        this.bookMarkCount = bookMarkCount;
        this.likeCount = likeCount;
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public void addReview (ReviewPost review) {
        this.reviews.add(review);
    }

    public void addHashtag (ContentHashtag contentHashtag) {
        this.contentHashtags.add(contentHashtag);
    }

    public void updateContent(ContentRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.imgUrl = requestDto.getImgUrl();
        this.description = requestDto.getDescription();
        this.author = requestDto.getAuthor();
        this.platform = requestDto.getPlatform();
        this.url = requestDto.getUrl();
        this.genre = requestDto.getGenre();
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
