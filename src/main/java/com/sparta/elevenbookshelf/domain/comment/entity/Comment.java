package com.sparta.elevenbookshelf.domain.comment.entity;

import com.sparta.elevenbookshelf.common.Timestamp;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamp {

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
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    private int likes;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(String contents, Content content , User user, Post post, Comment parent){
        this.contents = contents;
        this.user = user;
        this.post = post;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void updateContent(String contents) {
        this.contents = contents;
    }

    public void updateParent(Comment parent) {
        this.parent = parent;
    }

    public void updatePost(Post post) {
        this.post = post;
    }

    public void deleteChildren(Comment comment) {this.children.remove(comment);}

    public void addLikes(int likes) {this.likes = likes;}
}
