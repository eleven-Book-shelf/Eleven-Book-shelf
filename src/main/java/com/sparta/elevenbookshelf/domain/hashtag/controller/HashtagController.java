package com.sparta.elevenbookshelf.domain.hashtag.controller;

import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hashtag")
@Slf4j(topic = "HashtagController")
public class HashtagController {

    private final HashtagService hashtagService;

    // 해쉬 태그 탑 10 가져오기
    @GetMapping("/top10")
    public ResponseEntity<List<String>> readTop10Hashtags () {

        List<String> res = hashtagService.readTop10Hashtags();

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> readHashtags () {

        List<String> res = hashtagService.readHashtags();

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
