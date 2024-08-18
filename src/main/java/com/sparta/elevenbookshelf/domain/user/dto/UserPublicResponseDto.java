package com.sparta.elevenbookshelf.domain.user.dto;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPublicResponseDto {

    private Long id;
    private String nickname;
    private User.Status status;
    private User.Role role;
    private LocalDateTime createdAt;

    public UserPublicResponseDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.status = user.getStatus();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
