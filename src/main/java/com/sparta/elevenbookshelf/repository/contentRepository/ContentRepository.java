package com.sparta.elevenbookshelf.repository.contentRepository;

import com.sparta.elevenbookshelf.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ContentRepository extends JpaRepository<Content, Long>, QuerydslPredicateExecutor<Content>, ContentRepositoryCustom {
}
