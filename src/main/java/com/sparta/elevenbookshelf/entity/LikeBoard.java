package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LikeBoard extends Timestamp{

    @Id
    @GeneratedValue
    @Column(name = "like_board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public LikeBoard(User user, Board board) {
        this.user = user;
        this.board = board;
    }
}
