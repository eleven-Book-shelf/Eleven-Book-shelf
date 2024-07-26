package com.sparta.elevenbookshelf.entity;

import com.sparta.elevenbookshelf.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private List<Post> posts;

    @Builder
    public User(String username, String password,String email,String socialId ,Status status, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = username;
        this.email = email;
        this.socialId = socialId;
        this.status = status;
        this.role = role;
    }

    public void updateProfile(String username){
        this.nickname = username;
    }


    public void deleteRefreshToken() {
        this.refreshToken = "out";
    }

    public void addRefreshToken(String RefreshToken) {
        this.refreshToken = RefreshToken;
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
