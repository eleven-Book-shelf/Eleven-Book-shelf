package com.sparta.elevenbookshelf.domain.hashtag.controller;

import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hashtag")
@Slf4j(topic = "HashtagController")
public class HashtagController {

    private final HashtagService hashtagService;

}
