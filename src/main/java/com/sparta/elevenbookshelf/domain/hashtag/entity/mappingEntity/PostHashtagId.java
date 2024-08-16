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
public class PostHashtagId implements Serializable {

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "hashtag_id")
    private Long hashtagId;

    protected PostHashtagId (Long postId, Long hashtagId) {

        this.postId = postId;
        this.hashtagId = hashtagId;
    }
}
