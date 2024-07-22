package com.sparta.elevenbookshelf.dto;

import lombok.Builder;
import lombok.Data;

@Getter
@Data
@Builder

public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
