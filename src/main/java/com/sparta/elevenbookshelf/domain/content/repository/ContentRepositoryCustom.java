package com.sparta.elevenbookshelf.domain.content.repository;

import com.sparta.elevenbookshelf.domain.content.dto.ContentSearchCond;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContentRepositoryCustom {

    List<Content> findContentsBySearchCondition (long offset, int pagesize, ContentSearchCond cond);

    List<Content> findTopByView(long offset, int pageSize, Content.ContentType contentType, String genre);

    List<Content> findContentsByGenre(long offset, int pageSize,String platform, String genre);

    List<Content> findWebtoonContentsByGenre(long offset, int pageSize,String platform, String genre );

    List<Content> findWebnovelContentsByGenre(long offset, int pageSize,String platform, String genre);

    List<Content> findWebtoonContentsByGenreByUser(Long userId, long offset, int pageSize, String genre);

    List<Content> findWebnovelContentsByGenreByUser(Long userId, long offset, int pageSize, String genre);

    List<Content> findContentsByHashtagContainKeyword(String keyword, long offset, int pagesize);

    Page<Content> findreadSearchByKeyword(String keyword, int offset, int pagesize);

}
