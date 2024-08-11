package com.sparta.elevenbookshelf.domain.post.entity;

import com.sparta.elevenbookshelf.common.Timestamp;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 750)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int likes;

    private Double rating;

    @Version
    private Long version; // Optimistic Locking을 위한 버전 필드 추가

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Builder
    public Post (PostType type,String title, String body, User user, Double rating, Content content) {
        this.type = type;
        this.title = title;
        this.body = body;
        this.user = user;
        this.viewCount = 0;
        this.likes = 0;
        this.rating = rating;
        this.content = content;
    }

    public void updatePostType() {

        if (this.type.equals(PostType.NORMAL)) {
            this.type = PostType.REVIEW;
        } else {
            this.type = PostType.NORMAL;
        }
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateBody(String body) {
        this.body = body;
    }

    public void updateLikes(int likes) {this.likes = likes;}

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void addHashtags(Set<PostHashtag> hashtags) {
        this.postHashtags.addAll(hashtags);
    }

    //:::::::::::::::::// enum //::::::::::::::::://

    @Getter
    @RequiredArgsConstructor
    public enum PostType {
        NORMAL,
        REVIEW;
    }
}
