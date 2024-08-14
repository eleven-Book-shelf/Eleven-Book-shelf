package com.sparta.elevenbookshelf.domain.auth.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private final Long userId;

    private final String refreshToken;

    @TimeToLive
    private final long timeToLive;

    public RefreshToken(String refreshToken, Long userId, long timeToLive) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.timeToLive = timeToLive;
    }
}
