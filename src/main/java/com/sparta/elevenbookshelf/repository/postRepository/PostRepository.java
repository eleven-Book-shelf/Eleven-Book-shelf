package com.sparta.elevenbookshelf.repository.postRepository;

import com.sparta.elevenbookshelf.entity.post.ContentPost;
import com.sparta.elevenbookshelf.entity.post.NormalPost;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.entity.post.ReviewPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>,
        QuerydslPredicateExecutor<Post>, PostRepositoryCustom {

    Optional<ContentPost> findByContentId(Long contentId);

    @Query("SELECT p FROM NormalPost p")
    List<NormalPost> findAllNormalPost();

    @Query("SELECT p FROM ReviewPost p")
    List<ReviewPost> findAllReviewPost();

    @Query("SELECT p FROM ContentPost p")
    List<ContentPost> findAllContentPost();

}
