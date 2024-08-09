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
public class ContentHashtagId implements Serializable {

    @Column(name = "content")
    private Long contentId;

    @Column(name = "hashtag")
    private Long hashtagId;

    protected ContentHashtagId (Long contentId, Long hashtagId) {

        this.contentId = contentId;
        this.hashtagId = hashtagId;
    }
}
