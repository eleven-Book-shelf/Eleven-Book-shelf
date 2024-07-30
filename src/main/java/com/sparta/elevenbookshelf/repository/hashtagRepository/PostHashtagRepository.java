package com.sparta.elevenbookshelf.repository.hashtagRepository;

import com.sparta.elevenbookshelf.entity.mappingEntity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    Optional<PostHashtag> findByPostIdAndHashtagId(Long postId, Long hashtagId);
}
