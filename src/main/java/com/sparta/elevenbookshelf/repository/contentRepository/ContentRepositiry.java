package com.sparta.elevenbookshelf.repository.contentRepository;

import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Post;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ContentRepositiry extends JpaRepository<Content, Long>, QuerydslPredicateExecutor<Content>, ContentRepositoryCustom {
}
