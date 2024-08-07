package com.sparta.elevenbookshelf.domain.like.entity;

import com.sparta.elevenbookshelf.common.Timestamp;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class LikePost extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "psot_id")
    private Post post;

    @Builder
    public LikePost(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
