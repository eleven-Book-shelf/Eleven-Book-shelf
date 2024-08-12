package com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Scorable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContentHashtag implements Scorable {

    @EmbeddedId
    private ContentHashtagId id;

    @Builder.Default
    private double score = 1.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contentId")
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    public void createId() {
        this.id = new ContentHashtagId(content.getId(), hashtag.getId());
    }

    @Override
    public void incrementScore (double score) {
        this.score += score;
    }
}
