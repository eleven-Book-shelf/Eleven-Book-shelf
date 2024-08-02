package com.sparta.elevenbookshelf.repository.contentRepository;

import com.sparta.elevenbookshelf.entity.Content;

import java.util.List;

public interface ContentRepositoryCustom {

    List<Content> findTopByView(long offset, int pageSize ,Content.ContentType contentType ,String genre);

    List<Content> getContent(long offset, int pageSize, String genre);

    List<Content> getContentByConic(long offset, int pageSize, String genre);

    List<Content> getContentByNovel(long offset, int pageSize, String genre);

    List<Content> getContentByConicUser(Long userId, long offset, int pageSize, String genre);

    List<Content> getContentByNovelUser(Long userId, long offset, int pageSize, String genre);

    List<Content> search(int offset, int pagesize, String search);
}
