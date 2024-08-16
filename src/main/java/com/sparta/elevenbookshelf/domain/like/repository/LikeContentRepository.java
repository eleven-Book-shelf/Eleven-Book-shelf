package com.sparta.elevenbookshelf.domain.like.repository;

import com.sparta.elevenbookshelf.domain.like.entity.LikeContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeContentRepository extends JpaRepository<LikeContent, Long> {
    Optional<LikeContent> findByUserIdAndContentId(Long contentId, Long commentId);

    Boolean existsByContentIdAndUserId(Long contentId, Long userId);
}
