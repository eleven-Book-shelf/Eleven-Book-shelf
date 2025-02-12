package com.sparta.elevenbookshelf.domain.hashtag.repository;

import com.sparta.elevenbookshelf.domain.hashtag.entity.mappingEntity.ContentHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentHashtagRepository extends JpaRepository<ContentHashtag, Long> {

    Optional<ContentHashtag> findByContentIdAndHashtagId(Long contentId, Long hashtagId);
}
