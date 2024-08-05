package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.FavGenre;
import com.sparta.elevenbookshelf.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String nickname;
    private String email;
    private User.Status status;
    private User.Role role;
    private LocalDateTime createdAt;
    private List<String> favGenre = new ArrayList<>();


    public UserResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.favGenre.addAll(
                user.getFavGenres()
                        .stream()
                        .map(FavGenre::getGenre)
                        .toList());
    }
}
