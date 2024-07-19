package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private Board board;


    @Builder
    public Post(PostType postType, String title, String content) {
        this.postType = postType;
        this.title = title;
        this.content = content;
    }


    public void updatePostType(PostType postType) {
        this.postType = postType;
    }


    //:::::::::::::::::// enum //::::::::::::::::://

    @Getter
    @RequiredArgsConstructor
    public enum PostType{
        NORMAL,
        REVIEW
    }
}
