package com.sparta.elevenbookshelf.domain.content.controller;


import com.sparta.elevenbookshelf.domain.content.dto.ContentDataResponseDto;
import com.sparta.elevenbookshelf.domain.content.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.domain.content.service.ContentService;
import com.sparta.elevenbookshelf.domain.like.service.LikeService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;
    private final LikeService likeService;

    //::::::::::::::::::::::::// Normal //::::::::::::::::::::::::// Search

    // 매인 패이지의 컨탠츠 (웹툰 + 소설)
    @GetMapping
    public ResponseEntity<List<ContentDataResponseDto>> readContent(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContent(offset, pagesize, genre));
    }

    // 웹툰 패이지의 컨탠츠
    @GetMapping("/webtoon")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebtoon(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContentWebtoon(offset, pagesize, genre));
    }

    // 웹툰 패이지의 컨탠츠 탑 10
    @GetMapping("/webtoon/top")
    public ResponseEntity<List<ContentDataResponseDto>> readTopContentWebtoon(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "search", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContentWebtoonTop(offset, pagesize, genre));
    }

    // 소설 패이지의 컨탠츠
    @GetMapping("/webnovel")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebnovel(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContentWebnovel(offset, pagesize, genre));
    }

    // 소설 패이지의 컨탠츠 탑 10
    @GetMapping("/webnovel/top")
    public ResponseEntity<List<ContentDataResponseDto>> readTopContentWebnovel(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "search", required = false) String genre) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContentWebnovelTop(offset, pagesize, genre));
    }

    // 컨탠츠 상세 페이지
    @GetMapping("/{contentId}")
    public ResponseEntity<ContentResponseDto> readContentById(@PathVariable Long contentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContent(contentId));
    }

    // 컨탠츠 본 숫자
    @PostMapping("viewcount/{contentId}")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long contentId) {
        contentService.viewCount(contentId);
        return ResponseEntity.ok().build();
    }


    //::::::::::::::::::::::::// User Bookmark //:::::::::::::::::::::::://

    // 북마크 한 웹툰 패이지의 컨탠츠
    @GetMapping("/webtoon/bookmark")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebtoonUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContentWebtoonUser(userPrincipal.getUser().getId(), offset, pagesize, genre));
    }

    // 북마크 한 소설 패이지의 컨탠츠
    @GetMapping("/webnovel/bookmark")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebnovelUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contentService.readContentWebnovelUser(userPrincipal.getUser().getId(), offset, pagesize, genre));
    }

    //:::::::::::::::::// like //::::::::::::::::://

    // 컨탠츠 좋아요
    @PostMapping("like/{id}")
    public ResponseEntity<Void> createLikeContent(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.createLikeContent(id, userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 컨탠츠 좋아요취소
    @DeleteMapping("like/{id}")
    public ResponseEntity<Void> deleteLikeContent(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikeContent(id, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    // 컨탠츠 좋아요 수
    @GetMapping("like/{id}")
    public ResponseEntity<Boolean> getLikeContent(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(likeService.getLikeContent(id, userPrincipal.getUser().getId()));
    }

}
