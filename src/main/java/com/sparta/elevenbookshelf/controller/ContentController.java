package com.sparta.elevenbookshelf.controller;


import com.sparta.elevenbookshelf.dto.ContentDataResponseDto;
import com.sparta.elevenbookshelf.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class ContentController {

    private final ContentService contentService;

    //::::::::::::::::::::::::// normal //:::::::::::::::::::::::://Search

    @GetMapping
    public ResponseEntity<List<ContentDataResponseDto>> readContent(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContent(offset, pagesize, genre));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContentDataResponseDto>> Search(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "search", required = false) String search) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.contentSearch(offset, pagesize, search));
    }

    @GetMapping("/webtoon")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebtoon(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebtoon(offset, pagesize, genre));
    }

    @GetMapping("/webnovel")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebnovel(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebnovel(offset, pagesize, genre));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<ContentResponseDto> readComments(@PathVariable Long cardId) {
        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContent(cardId));
    }

    @PostMapping("/{cardId}/viewcount")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long cardId) {
        contentService.viewCount(cardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hashtag")
    public ResponseEntity<Set<String>> hashtag(){
        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.getAllContentHashTags());
    }

    //::::::::::::::::::::::::// User BookMark //:::::::::::::::::::::::://

    @GetMapping("/webtoon/bookmark")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebtoonUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebtoonUser(userPrincipal.getUser().getId(), offset, pagesize, genre));
    }

    @GetMapping("/webnovel/bookmark")
    public ResponseEntity<List<ContentDataResponseDto>> readContentWebnovelUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebnovelUser(userPrincipal.getUser().getId(), offset, pagesize, genre));
    }

}
