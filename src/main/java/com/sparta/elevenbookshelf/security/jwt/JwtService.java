package com.sparta.elevenbookshelf.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    @Value("${jwt.secret-key}")
    private String SecretKey;

    @Value("${jwt.access-expire-time}")
    private long accessExpireTime;

    @Value("${jwt.refresh-expire-time}")
    private long refreshExpireTime;

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(SecretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 엑세스 토큰
    public String generateAccessToken(String username) {
        return createToken(username, accessExpireTime);

    }

    // 리프레쉬 토큰
    public String generateRefreshToken(String username) {

        return createToken(username, refreshExpireTime);

    }

    // 토큰 생성
    private String createToken(String username, Long expirationTime) {

        Date now = new Date();
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(key, signatureAlgorithm)
                .compact();

        log.info("CreateToken 메서드로 생성된 토큰 : " + token);
        return token;
    }



}
