package com.sparta.elevenbookshelf.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    private boolean status;
    private LocalDateTime createDate;

    @Builder
    public BookMark(User user, Content content, boolean status) {
        this.user = user;
        this.content = content;
        this.status = status;
        this.createDate = LocalDateTime.now();
    }

    public void toggleStatus() {
        this.status = !this.status;
    }
}
