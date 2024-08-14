package com.sparta.elevenbookshelf.domain.like.repository;

import com.sparta.elevenbookshelf.domain.like.entity.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikePostRepository extends JpaRepository<LikePost, Long> {

    int countByPostId(Long id);

    boolean existsByUserIdAndPostId(Long userId, Long id);

    Optional<LikePost> findByUserIdAndPostId(Long userId, Long id);
}
