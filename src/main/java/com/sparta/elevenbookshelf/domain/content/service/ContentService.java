package com.sparta.elevenbookshelf.domain.content.service;

import com.sparta.elevenbookshelf.domain.content.dto.*;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    //::::::::::::::::::::::::// Create & Update //:::::::::::::::::::::::://

    private static List<ContentResponseDto> getContentResponseDtos(List<Content> contents) {
        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());
    }

    public Content createContent(ContentRequestDto req) {

        return contentRepository.saveAndFlush(new Content(req));
    }


    //::::::::::::::::::::::::// Read //:::::::::::::::::::::::://

    public void updateContent(Content content, ContentRequestDto req) {

        content.updateContent(req);

        contentRepository.saveAndFlush(content);
    }

    public ContentResponseDto readContent(Long contentId) {

        Content content = getContent(contentId);
        content.incrementView();

        return new ContentResponseDto(content);
    }

    /**
     * 컨텐츠 검색 기능
     * - 주어진 조건들에 맞춰 컨텐츠를 조회합니다.
     *
     * @param offset        현재 위치
     * @param pagesize      페이지 사이즈
     * @return List<ContentResponseDto> 불러온 컨텐츠 Dto 목록
     * @Body : ContentsearchCond
     * isBookmarked 사용자의 북마크 조건으로 필터링 할 것인지 여부 : 기본 값 : "f"
     * keyword 검색할 키워드 : 비어있으면 전체 조회
     * contentType WEBTOON || WEBNOVEL : 비어있으면 전체 조회
     * sortBy 정렬조건 : 비어있으면 조회수 순 정렬
     */
//    public List<ContentResponseDto> readContents(long offset, int pagesize, Long userId, String genre, String contentType, String sortBy) {
    public List<ContentResponseDto> readContents(long offset, int pagesize, ContentSearchCond cond) {

        List<Content> contents = contentRepository.findContentsBySearchCondition(offset, pagesize, cond);

        return contents.stream()
                .map(ContentResponseDto::new)
                .collect(Collectors.toList());

    }

    public List<ContentResponseDto> readContentsByGenre(int offset, int pagesize, String platform, String genre, String end) {

        Content.ContentEnd isEnd = getContentEnd(end);

        List<Content> contents = contentRepository.findContentsByGenre(offset, pagesize, platform, genre, isEnd);

        return getContentResponseDtos(contents);
    }

    public List<ContentResponseDto> readWebtoonContents(String contentType, int offset, int pagesize, String platform, String genre, String end) {

        Content.ContentType type = getContentType(contentType);
        Content.ContentEnd isEnd = getContentEnd(end);

        List<Content> contents = contentRepository.findContentsByGenre(type, offset, pagesize, platform, genre, isEnd);
        return getContentResponseDtos(contents);
    }

    // OrderByViewDESC //


    public List<ContentResponseDto> readContentsOrderByView(String contentType, int offset, int pagesize, String genre) {

        Content.ContentType type = Content.ContentType.valueOf(contentType);

        List<Content> contents = contentRepository.findTopByView(offset, pagesize, type, genre);

        return getContentResponseDtos(contents);
    }

    //::::::::::::::::::::::::// User BookMark //:::::::::::::::::::::::://

    public List<ContentResponseDto> readContentsByKeyword(int offset, int pagesize, String keyword) {

        List<Content> contents = contentRepository.findContentsByHashtagContainKeyword(keyword, offset, pagesize);

        return getContentResponseDtos(contents);
    }

    public List<ContentResponseDto> readWebtoonContentsByUser(Long userId, int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findWebtoonContentsByGenreByUser(userId, offset, pagesize, genre);

        return getContentResponseDtos(contents);
    }

    public List<ContentResponseDto> readWebnovelContentsByUser(Long userId, int offset, int pagesize, String genre) {

        List<Content> contents = contentRepository.findWebnovelContentsByGenreByUser(userId, offset, pagesize, genre);

        return getContentResponseDtos(contents);
    }

    public List<Content> getContents() {
        return contentRepository.findAll();
    }

    public Set<String> getAllContentHashTags() {
        List<String> contentHashTagsList = contentRepository.findAllByContentHashTag();

        return contentHashTagsList.stream()
                .flatMap(tags -> Arrays.stream(tags.split("#")))
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());
    }

    public Page<ContentAdminResponseDto> getContentPage(int page, int size, String sortBy, boolean asc) {

        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Content> contents = contentRepository.findAll(pageable);

        return contents.map(ContentAdminResponseDto::new);
    }

    public void updateContentPage(Long contentId) {
        contentRepository.deleteById(contentId);
    }


    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public ContentMapResponseDto readSearchByKeyword(String keyword, int offset, int pagesize) {
        Page<Content> contents = contentRepository.findreadSearchByKeyword(keyword, offset, pagesize);

        return new ContentMapResponseDto(contents.getTotalPages(), contents.getContent().stream()
                .map(ContentResponseDto::new)
                .toList());
    }

    public Content getContent(Long contentId) {
        return contentRepository.findById(contentId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT)
        );
    }

    public Content.ContentType getContentType(String contentType) {
        return Optional.ofNullable(contentType)
                .filter(value -> !value.trim().isEmpty())
                .map(value -> Content.ContentType.valueOf(value.toUpperCase()))
                .orElse(null);
    }

    public Content.ContentEnd getContentEnd(String end) {
        return Optional.ofNullable(end)
                .filter(value -> !value.trim().isEmpty())
                .map(value -> Content.ContentEnd.valueOf(value.toUpperCase()))
                .orElse(null);
    }


}
