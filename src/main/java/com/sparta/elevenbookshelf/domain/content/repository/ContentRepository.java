package com.sparta.elevenbookshelf.domain.content.repository;

import com.sparta.elevenbookshelf.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long>, QuerydslPredicateExecutor<Content>, ContentRepositoryCustom {

    Optional<Content> findByUrl(String artUrl);

    List<Content> findByType(Content.ContentType type);

    @Query(value = "SELECT content.content_hash_tag FROM content", nativeQuery = true)
    List<String> findAllByContentHashTag();

}
