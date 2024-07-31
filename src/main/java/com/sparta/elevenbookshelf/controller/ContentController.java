package com.sparta.elevenbookshelf.controller;


import com.sparta.elevenbookshelf.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class ContentController {

    private final ContentService contentService;

    //::::::::::::::::::::::::// normal //:::::::::::::::::::::::://

    @GetMapping("/webtoon")
    public ResponseEntity<List<ContentResponseDto>> readContentWebtoon(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebtoon(offset, pagesize, genre));
    }

    @GetMapping("/webnovel")
    public ResponseEntity<List<ContentResponseDto>> readContentWebnovel(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebnovel(offset, pagesize, genre));
    }

    @GetMapping
    public ResponseEntity<List<ContentResponseDto>> readContent(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContent(offset, pagesize, genre));
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

    //::::::::::::::::::::::::// User BookMark //:::::::::::::::::::::::://

    @GetMapping("/webtoon/bookmark")
    public ResponseEntity<List<ContentResponseDto>> readContentWebtoonUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebtoonUser(userPrincipal.getUser().getId(), offset, pagesize, genre));
    }

    @GetMapping("/webnovel/bookmark")
    public ResponseEntity<List<ContentResponseDto>> readContentWebnovelUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "4") int pagesize,
            @RequestParam(value = "genre", required = false) String genre) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContentWebnovelUser(userPrincipal.getUser().getId(), offset, pagesize, genre));
    }

}
