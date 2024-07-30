package com.sparta.elevenbookshelf.repository.hashtagRepository;

import com.sparta.elevenbookshelf.entity.mappingEntity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
