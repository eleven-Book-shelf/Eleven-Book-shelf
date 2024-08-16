package com.sparta.elevenbookshelf.domain.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String username;
    private String password;
}
