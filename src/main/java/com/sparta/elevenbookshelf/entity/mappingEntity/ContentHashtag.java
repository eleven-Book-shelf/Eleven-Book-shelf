package com.sparta.elevenbookshelf.entity.mappingEntity;

import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.Timestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContentHashtag extends Timestamp {

    @EmbeddedId
    private ContentHashtagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("contentId")
    @JoinColumn(name = "conten_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    public void createId() {
        this.id = new ContentHashtagId(content.getId(), hashtag.getId());
    }
}
