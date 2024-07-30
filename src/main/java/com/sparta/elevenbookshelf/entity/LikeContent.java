package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LikeContent extends Timestamp{

    @Id
    @GeneratedValue
    @Column(name = "like_board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @Builder
    public LikeContent(User user, Content content) {
        this.user = user;
        this.content = content;
    }
}
