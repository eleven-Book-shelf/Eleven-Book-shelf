package com.sparta.elevenbookshelf.dto;

import com.sparta.elevenbookshelf.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String password;
    private String email;
    private User.Status status;
    private User.Role role;
}
