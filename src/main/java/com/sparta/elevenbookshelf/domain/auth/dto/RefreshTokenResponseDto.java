package com.sparta.elevenbookshelf.domain.auth.dto;

import com.sparta.elevenbookshelf.domain.auth.entity.RefreshToken;
import lombok.Data;

@Data
public class RefreshTokenResponseDto {
    private String token;

    public RefreshTokenResponseDto(RefreshToken refreshToken) {
        this.token = refreshToken.getRefreshToken();
    }
}
