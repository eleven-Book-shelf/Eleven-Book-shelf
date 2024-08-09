package com.sparta.elevenbookshelf.domain.content.service;

import com.sparta.elevenbookshelf.domain.content.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.domain.content.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    //::::::::::::::::::::::::// Create & Update //:::::::::::::::::::::::://

    public Content createContent(ContentRequestDto req) {

        return contentRepository.saveAndFlush(new Content(req));
    }

    public void updateContent(Content content, ContentRequestDto req) {

        content.updateContent(req);

        contentRepository.saveAndFlush(content);
    }


    //::::::::::::::::::::::::// Read //:::::::::::::::::::::::://

    public ContentResponseDto readContent(Long contentId) {

        Content content = getContent(contentId);
        content.incrementView();

        return new ContentResponseDto(content);
    }

    /**
     * 컨텐츠 검색 기능
     * - 주어진 조건들에 맞춰 컨텐츠를 조회합니다.
     * @param offset 현재 위치
     * @param pagesize 페이지 사이즈
     * @param userId 사용자 ID
     * @param genre 검색할 키워드
     * @param contentType WEBTOON || WEBNOVEL || ALL
     * @param sortBy 정렬조건
     * @return List<ContentResponseDto> 불러온 컨텐츠 Dto 목록
     */
    public List<ContentResponseDto> readContents (long offset, int pagesize, Long userId, String genre, String contentType, String sortBy) {

        List<Content> contents = contentRepository.findContentsBySearchCondition(offset, pagesize, userId, genre, contentType, sortBy);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());

    }

    public List<ContentResponseDto> readContentsByGenre(int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findContentsByGenre(offset, pagesize, genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readWebtoonContents(int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findWebtoonContentsByGenre(offset, pagesize, genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readWebnovelContents(int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findWebnovelContentsByGenre(offset, pagesize, genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

        // OrderByViewDESC //

    public List<ContentResponseDto> readContentsOrderByView(int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findTopByView(offset, pagesize,null, genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readWebtoonContentsOrderByView(int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findTopByView(offset, pagesize,Content.ContentType.COMICS,genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readWebnovelContentsOrderByView(int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findTopByView(offset, pagesize,Content.ContentType.NOVEL,genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readContentsByKeyword(int offset, int pagesize, String keyword) {

        List<Content> contents = contentRepository.findContentsByHashtagContainKeyword(keyword, offset, pagesize);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    //::::::::::::::::::::::::// User BookMark //:::::::::::::::::::::::://

    public List<ContentResponseDto> readWebtoonContentsByUser(Long userId, int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findWebtoonContentsByGenreByUser(userId, offset, pagesize, genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDto> readWebnovelContentsByUser(Long userId, int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findWebnovelContentsByGenreByUser(userId, offset, pagesize , genre);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public Content getContent(Long contentId) {
        return contentRepository.findById(contentId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT)
        );
    }
}
