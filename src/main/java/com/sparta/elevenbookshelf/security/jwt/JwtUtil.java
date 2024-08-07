package com.sparta.elevenbookshelf.security.jwt;

import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
@Slf4j(topic = "JwtUtil")
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String SecretKey;
    private Key key;

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(SecretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public boolean isTokenValidate(String token) {
        log.info("isTokenValidate 메서드 실행. 검사할 토큰 : " + token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            return handleExpiredToken(e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    private boolean handleExpiredToken(ExpiredJwtException e) {
        String username = e.getClaims().getSubject();
        log.info("토큰 사용자 이름: {}", username);
        String refreshToken = getRefreshToken(username);
        return isRefreshTokenValidate(refreshToken);
    }

    public boolean isRefreshTokenValidate(String token) {
        log.info("isRefreshTokenValidate 메서드 실행. 검사할 토큰 : " + token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 리프레쉬 토큰 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT 리프레쉬 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 리프레쉬 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 리프레쉬 토큰 입니다.");
        }
        return false;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getErrorMessage(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return "정상 JWT token 입니다.";
        } catch (JwtException e) {
            return "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.";
        } catch (IllegalArgumentException e) {
            return "JWT claims is empty, 잘못된 JWT 토큰 입니다.";
        }
    }

    private String getRefreshToken(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        ).getRefreshToken();
    }
}
