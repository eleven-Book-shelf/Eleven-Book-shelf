package com.sparta.elevenbookshelf.repository.contentRepository;

import com.sparta.elevenbookshelf.entity.Content;

import java.util.List;

public interface ContentRepositoryCustom {
    List<Content> findTop50ByView();
}
