package com.sparta.elevenbookshelf.entity;

import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    private String description;

    private String author;

    private String platform;

    private Double view;

    private Double rating;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Enumerated(EnumType.STRING)
    private ContentEnd isEnd;

    @OneToMany(mappedBy = "content", cascade = CascadeType.MERGE)
    private List<ReviewPost> reviews = new ArrayList<>();

    @Builder
    public Content(String title, String description, String author, String platform, String imgurl, Double view, Double rating, ContentType type, ContentEnd isEnd) {

        this.title = title;
        this.imgurl = imgurl;
        this.description = description;
        this.author = author;
        this.platform = platform;
        this.view = view;
        this.rating = rating;
        this.type = type;
        this.isEnd = isEnd;
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public void addReview(ReviewPost review) {
        this.reviews.add(review);
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
