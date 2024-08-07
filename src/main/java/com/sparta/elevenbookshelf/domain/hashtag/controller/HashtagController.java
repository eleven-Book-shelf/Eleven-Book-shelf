package com.sparta.elevenbookshelf.domain.hashtag.controller;

import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserHashtagRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserHashtagResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hashtag")
@Slf4j(topic = "HashtagController")
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping
    public ResponseEntity<List<UserHashtagResponseDto>> getUserHashtags(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        log.info("getUserHashtag 실행");
        List<UserHashtagResponseDto> res = hashtagService.readUserHashtags(userPrincipal.getUser().getId(), limit);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping
    public ResponseEntity<List<UserHashtagResponseDto>> updateUserHashtags(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                           @RequestBody UserHashtagRequestDto req) {
        log.info("updateUserHashtag 실행");
        List<UserHashtagResponseDto> res = hashtagService.updateUserHashtags(userPrincipal.getUser().getId(), req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<PostResponseDto>> recommendContentsByUserHashtag (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") long offset,
            @RequestParam(defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = hashtagService.recommendContentByUserHashtag(userPrincipal.getUser().getId(), offset, pagesize);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/top10")
    public ResponseEntity<List<String>> readTop10Hashtags () {

        List<String> res = hashtagService.readTop10Hashtags();

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
