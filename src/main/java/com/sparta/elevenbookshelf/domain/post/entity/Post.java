package com.sparta.elevenbookshelf.domain.post.entity;

import com.sparta.elevenbookshelf.domain.board.entity.Board;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type", discriminatorType = DiscriminatorType.STRING)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 750)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @OneToMany(mappedBy = "post")
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likes;

    public Post (String title, String body, User user, Board board, Content content) {
        this.title = title;
        this.body = body;
        this.user = user;
        this.board = board;
        this.content = content;
        this.viewCount = 0;
        this.likes = 0; // 초기 조회수는 0으로 설정
    }

    public String getPostType() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateBody(String body) {
        this.body = body;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateBoard(Board board) {
        this.board = board;
    }

    public void addHashtag(PostHashtag postHashtag) {
        this.postHashtags.add(postHashtag);
    }

    public void addLikes(int likes) {this.likes = likes;}

    public void incrementViewCount() {
        this.viewCount++;
    }
}
