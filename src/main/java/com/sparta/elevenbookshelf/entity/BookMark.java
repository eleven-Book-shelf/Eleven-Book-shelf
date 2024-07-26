package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class BookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private boolean status;
    private LocalDateTime createDate;

    @Builder
    public BookMark(User user, Post post, boolean status) {
        this.user = user;
        this.post = post;
        this.status = status;
        this.createDate = LocalDateTime.now();
    }

    public void toggleStatus() {
        this.status = !this.status;
    }
}
