package com.sparta.elevenbookshelf.entity.mappingEntity;

import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.Timestamp;
import com.sparta.elevenbookshelf.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserHashtag {

    @EmbeddedId
    private UserHashtagId id;

    @Builder.Default
    private double score = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;


    public void createId() {
        this.id = new UserHashtagId(user.getId(), hashtag.getId());
    }

    public void incrementScore(double score) {
        this.score += score;
    }

    public void decrementScore(double score) {
        this.score -= score;
    }
}