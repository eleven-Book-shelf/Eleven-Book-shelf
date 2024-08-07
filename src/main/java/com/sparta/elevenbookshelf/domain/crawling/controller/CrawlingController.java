package com.sparta.elevenbookshelf.domain.crawling.controller;

import com.sparta.elevenbookshelf.domain.content.dto.ContentDataResponseDto;
import com.sparta.elevenbookshelf.domain.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content")
public class CrawlingController {

    private final CrawlingService crawlingService;

    // 전체 크롤링 시작.
    @PostMapping("/start/all")
    public ResponseEntity<Void> allCrawlingStart() {

        crawlingService.allCrawlingStart();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 로컬 파일로 저장.
    @GetMapping("/local")
    public ResponseEntity<Void> csvFileToLocal() {

        crawlingService.csvFileToLocal();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 로컬 파일로 DB 업데이트.
    @PostMapping("/local/update")
    public ResponseEntity<Void> localToDataBase() {

        crawlingService.localToDataBase();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Content 단건 조회
    @GetMapping
    public ResponseEntity<ContentDataResponseDto> getOneContent(@RequestParam String contentUrl) {

        String decodeUrl = URLDecoder.decode(contentUrl, StandardCharsets.UTF_8);

        ContentDataResponseDto responseDto = crawlingService.getOneContent(decodeUrl);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
