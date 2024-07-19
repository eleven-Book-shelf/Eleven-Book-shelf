package com.sparta.elevenbookshelf.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
