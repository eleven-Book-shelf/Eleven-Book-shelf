package com.sparta.elevenbookshelf.domain.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
