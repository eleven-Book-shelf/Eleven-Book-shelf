package com.sparta.elevenbookshelf.domain.content.controller;


import com.sparta.elevenbookshelf.domain.content.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.domain.content.service.ContentService;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.like.service.LikeService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;
    private final LikeService likeService;
    private final HashtagService hashtagService;

    // 컨텐츠 상세 페이지
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentResponseDto> readContent(
            @AuthenticationPrincipal @Nullable UserPrincipal userPrincipal,
            @PathVariable Long contentId) {

        ContentResponseDto res = contentService.readContent(contentId);

        if (userPrincipal != null) {
            hashtagService.userContentHashtagInteraction(userPrincipal.getUser().getId(), contentId,
                    hashtagService.READ_WEIGHT, hashtagService.READED_WEIGHT);
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    /**
     * 컨텐츠 검색 기능
     * - 주어진 조건들에 맞춰 컨텐츠를 조회합니다.
     * @param userPrincipal 사용자 정보 : Nullable
     * @param isBookmarked 사용자의 북마크 조건으로 필터링 할 것인지 여부 : 기본 값 : "f"
     * @param genre 검색할 키워드 : 비어있으면 전체 조회
     * @param contentType WEBTOON || WEBNOVEL : 비어있으면 전체 조회
     * @param sortBy 정렬조건 : 비어있으면 조회수 순 정렬
     * @param offset 현재 위치
     * @param pagesize 페이지 사이즈
     * @return List<ContentResponseDto> 불러온 컨텐츠 Dto 목록
     */
    @GetMapping("/")
    public ResponseEntity<List<ContentResponseDto>> readContentsByCondition (
            @AuthenticationPrincipal @Nullable UserPrincipal userPrincipal,
            @RequestParam(value = "isBookmarked", defaultValue = "f", required = false) String isBookmarked,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "contentType", required = false) String contentType,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {


        Long userId = null;

        if (isBookmarked.equals("t")) {
            if(userPrincipal != null) {
                userId = userPrincipal.getUser().getId();
            }
        }

        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, userId, genre, contentType, sortBy);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<ContentResponseDto>> recommendContentsByUserHashtag (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") long offset,
            @RequestParam(defaultValue = "20") int pagesize) {

        List<ContentResponseDto> res = hashtagService.recommendContentByUserHashtag(userPrincipal.getUser(),
                contentService.getContents() ,offset, pagesize);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 매인 페이지의 컨텐츠 (웹툰 + 소설)
    @GetMapping
    public ResponseEntity<List<ContentResponseDto>> readContents(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readContentsByGenre(offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, null, genre, null, null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 웹툰 페이지의 컨텐츠
    @GetMapping("/webtoon")
    public ResponseEntity<List<ContentResponseDto>> readContentWebtoon(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readWebtoonContents(offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, null, genre, "COMICS", null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 웹툰 페이지의 컨텐츠 탑 10
    @GetMapping("/webtoon/top")
    public ResponseEntity<List<ContentResponseDto>> readTopContentWebtoon(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "search", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readWebtoonContentsOrderByView(offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, null, genre, "COMICS", null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 소설 페이지의 컨텐츠
    @GetMapping("/webnovel")
    public ResponseEntity<List<ContentResponseDto>> readContentWebnovel(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readWebnovelContents(offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, null, genre, "NOVEL", null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 소설 페이지의 컨텐츠 탑 10
    @GetMapping("/webnovel/top")
    public ResponseEntity<List<ContentResponseDto>> readTopContentWebnovel(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "search", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readWebnovelContentsOrderByView(offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, null, genre, "NOVEL", null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }



    //::::::::::::::::::::::::// User Bookmark //:::::::::::::::::::::::://

    // 북마크 한 웹툰 페이지의 컨텐츠
    @GetMapping("/webtoon/bookmark")
    public ResponseEntity<List<ContentResponseDto>> readContentWebtoonUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readWebtoonContentsByUser(userPrincipal.getUser().getId(), offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, userPrincipal.getUser().getId(), genre, "COMICS", null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 북마크 한 소설 페이지의 컨텐츠
    @GetMapping("/webnovel/bookmark")
    public ResponseEntity<List<ContentResponseDto>> readContentWebnovelUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        List<ContentResponseDto> res = contentService.readWebnovelContentsByUser(userPrincipal.getUser().getId(), offset, pagesize, genre);
//        List<ContentResponseDto> res = contentService.readContents(offset, pagesize, userPrincipal.getUser().getId(), genre, "NOVEL", null);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    //:::::::::::::::::// like //::::::::::::::::://

    // 컨텐츠 좋아요
    @PostMapping("/{contentId}/like")
    public ResponseEntity<Void> createLikeContent(
            @PathVariable Long contentId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.createLikeContent(contentId, userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 컨텐츠 좋아요 취소
    @DeleteMapping("/{contentId}/like")
    public ResponseEntity<Void> deleteLikeContent(
            @PathVariable Long contentId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikeContent(contentId, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    // 컨탠츠 좋아요 수
    @GetMapping("/{contentId}/like")
    public ResponseEntity<Boolean> getLikeContent(
            @PathVariable Long contentId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(likeService.getLikeContent(contentId, userPrincipal.getUser().getId()));
    }

}
