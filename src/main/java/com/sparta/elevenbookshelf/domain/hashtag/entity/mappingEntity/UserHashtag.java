package com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity;

import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Scorable;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserHashtag implements Scorable {

    @EmbeddedId
    private UserHashtagId id;

    @Builder.Default
    private double score = 1.0;

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

    @Override
    public void incrementScore(double score) {
        this.score += score;
    }
}