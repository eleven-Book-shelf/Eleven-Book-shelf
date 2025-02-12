package com.sparta.elevenbookshelf.domain.hashtag.entity;

import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.UserHashtag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    private double tier;

    private String tag;

    private int count;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserHashtag> userHashtags = new HashSet<>();

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentHashtag> contentHashtags = new HashSet<>();

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @Builder
    public Hashtag(String tag) {
        this.tier = 0;
        this.tag = tag;
        this.count = 0;
    }

    public void incrementCount() {
        this.count++;
        this.tier = count/10.0;
    }
}
