package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(String contents, User user, Board board, Comment parent){
        this.contents = contents;
        this.user = user;
        this.board = board;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void updateContent(String contents) {
        this.contents = contents;
    }

    public void updateParent(Comment parent) {
        this.parent = parent;
    }

    public void updateBoard(Board board) {
        this.board = board;
    }

}
