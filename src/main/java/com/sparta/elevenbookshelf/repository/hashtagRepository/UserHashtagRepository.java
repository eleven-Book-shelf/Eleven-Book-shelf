package com.sparta.elevenbookshelf.repository.hashtagRepository;

import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHashtagRepository extends JpaRepository<UserHashtag, Long> {
}
