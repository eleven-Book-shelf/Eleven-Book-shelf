package com.sparta.elevenbookshelf.security.jwt;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j(topic = "JwtService")
public class JwtService {

    @Value("${jwt.key}")
    private String SecretKey;

    @Value("${jwt.access-expire-time}")
    private long accessExpireTime;

    @Value("${jwt.refresh-expire-time}")
    private long refreshExpireTime;

    private Key key;
    public static String CLAIM_ID = "id";
    public static String CLAIM_ROLE = "role";
    public static String CLAIM_STATUS = "status";

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {

        byte[] bytes = Base64.getDecoder().decode(SecretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 엑세스 토큰
    public String generateAccessToken(User user) {

        log.info("generateAccessToken 메서드 실행");
        return createToken(user.getUsername(),user.getRole(),user.getId(),user.getStatus(), accessExpireTime);

    }

    // 리프레쉬 토큰
    public String generateRefreshToken(User user) {

        log.info("generateRefreshToken 메서드 실행");
        return createToken(user.getUsername(),user.getRole(),user.getId(),user.getStatus(), refreshExpireTime);

    }

    // 토큰 생성
    private String createToken(String username,User.Role role, Long id, User.Status status, Long expirationTime) {
        log.info("createToken 메서드 실행");
        Date now = new Date();
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .claim(CLAIM_ID, id)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_STATUS, status)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(key, signatureAlgorithm)
                .compact();

        log.info("createToken 메서드로 생성된 토큰 : {}", token);
        return token;
    }

    //토큰 검증
    public boolean isTokenValidate(String token) {
        log.info("isTokenValidate 메서드 실행. 검사할 토큰 : {}", token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public boolean isRefreshTokenValidate(String token) {
        log.info("isRefreshTokenValidate 메서드 실행. 검사할 토큰 : {}", token);
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

    public String getUsernameFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
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
}
