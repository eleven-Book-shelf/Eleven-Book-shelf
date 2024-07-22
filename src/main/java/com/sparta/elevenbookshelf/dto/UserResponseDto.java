package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String username;
    private String password;
    private String email;
    private User.Status status;
    private User.Role role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.role = user.getRole();
    }
}
