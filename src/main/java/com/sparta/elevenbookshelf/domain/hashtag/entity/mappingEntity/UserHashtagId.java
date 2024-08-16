package com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
@EqualsAndHashCode
public class UserHashtagId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hashtag_id")
    private Long hashtagId;

    protected UserHashtagId (Long userId, Long hashtagId) {

        this.userId = userId;
        this.hashtagId = hashtagId;
    }
}
