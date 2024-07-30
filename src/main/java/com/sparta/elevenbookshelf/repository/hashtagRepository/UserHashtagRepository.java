package com.sparta.elevenbookshelf.repository.hashtagRepository;

import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Long> {

    Optional<UserHashtag> findByUserIdAndHashtagId(Long userId, Long hashtagId);
}
