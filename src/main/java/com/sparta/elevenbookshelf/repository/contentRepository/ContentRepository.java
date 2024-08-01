package com.sparta.elevenbookshelf.repository.contentRepository;

import com.sparta.elevenbookshelf.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long>, QuerydslPredicateExecutor<Content>, ContentRepositoryCustom {

    Optional<Content> findByUrl(String artUrl);

    List<Content> findByType(Content.ContentType type);
}
