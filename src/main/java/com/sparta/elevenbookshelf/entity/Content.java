package com.sparta.elevenbookshelf.entity;

import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
