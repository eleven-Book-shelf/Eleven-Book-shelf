package com.sparta.elevenbookshelf.repository.contentRepository;

import com.sparta.elevenbookshelf.entity.Content;

import java.util.List;

public interface ContentRepositoryCustom {
    List<Content> findTop50ByView();

    List<Content> getContentByConic(long offset, int pagesize, String genre);

    List<Content> getContentByNovel(long offset, int pagesize, String genre);

    List<Content> getContentByConicUser(Long userId, long offset, int pagesize);

    List<Content> getContentByNovelUser(Long userId, long offset, int pagesize);

}
