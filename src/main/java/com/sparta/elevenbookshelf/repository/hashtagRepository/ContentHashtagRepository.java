package com.sparta.elevenbookshelf.repository.hashtagRepository;

import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentHashtagRepository extends JpaRepository<ContentHashtag, Long> {
}
