package com.sparta.elevenbookshelf.domain.auth.service;

import com.sparta.elevenbookshelf.domain.auth.entity.RefreshToken;
import com.sparta.elevenbookshelf.domain.auth.repository.RefreshTokenRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${REFRESH_EXPIRE_TIME}")
    private Long refreshExpireTime;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String refreshToken, Long userId) {
        return new RefreshToken(refreshToken, userId, refreshExpireTime);
    }

    public String refreshToken(Long userId) {
        return refreshTokenRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        ).getRefreshToken();

    }

    public void saveRefreshToken(String refreshToken,Long userId) {
        refreshTokenRepository.save(new RefreshToken(refreshToken, userId, refreshExpireTime));
    }

    public void deleteRefreshToken(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
        refreshTokenRepository.delete(refreshToken);
    }


}
