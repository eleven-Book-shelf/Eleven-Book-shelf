package com.sparta.elevenbookshelf.entity;

import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.entity.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String username;

    private String nickname;

    private String password;

    @Column(unique = true)
    private String email;

    private String refreshToken;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy ="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy ="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavGenre> favGenres = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private Set<UserHashtag> userHashtags = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Builder
    public User(String username,String nickname ,String password,String email,String socialId ,Status status, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.socialId = socialId;
        this.status = status;
        this.role = role;
    }

    public void updateProfile(String username){
        this.nickname = username;
    }

    public void signOut(){
        this.status = Status.DELETED;
    }

    public void deleteRefreshToken() {
        this.refreshToken = "out";
    }

    public void addRefreshToken(String RefreshToken) {
        this.refreshToken = RefreshToken;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public void addHashtag(UserHashtag userHashtag) {
        this.userHashtags.add(userHashtag);
    }

    public void addFavGenre(List<FavGenre> favGenres) {
        this.favGenres.addAll(favGenres);
    }
    public void removeFavGenre(List<FavGenre> favGenres) {
        this.favGenres.removeAll(favGenres);
    }

    //:::::::::::::::::// enum //::::::::::::::::://

    @Getter
    @RequiredArgsConstructor
    public enum Role{
        USER,
        ADMIN
    }

    @Getter
    @RequiredArgsConstructor
    public enum Status{
        NORMAL,
        DELETED,
        BLOCKED
    }

    public boolean isBlocked(){
        return this.status == Status.DELETED || this.status == Status.BLOCKED;
    }

    @Transient
    public boolean isActivity() {
        return this.status == Status.NORMAL;
    }

}
