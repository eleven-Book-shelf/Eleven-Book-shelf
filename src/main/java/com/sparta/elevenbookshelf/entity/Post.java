package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private Board board;


    @Builder
    public Post(PostType postType, String title, String content, User user, Board board) {

        this.postType = postType;
        this.title = title;
        this.content = content;
        this.user = user;
        this.board = board;
    }

    public void updatePostType(PostType postType) {
        this.postType = postType;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateBoard(Board board) {
        this.board = board;
    }


    //:::::::::::::::::// enum //::::::::::::::::://

    @Getter
    @RequiredArgsConstructor
    public enum PostType{
        NORMAL,
        REVIEW
    }
}
