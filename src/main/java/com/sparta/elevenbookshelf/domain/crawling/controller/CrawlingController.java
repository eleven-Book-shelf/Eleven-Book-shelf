package com.sparta.elevenbookshelf.domain.crawling.controller;

import com.sparta.elevenbookshelf.domain.content.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.domain.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/start/r")
    public ResponseEntity<Void> rCrawlingStart() {

        crawlingService.rCrawlingStart();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/start/m")
    public ResponseEntity<Void> mCrawlingStart() {

        crawlingService.mCrawlingStart();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/start/k")
    public ResponseEntity<Void> kCrawlingStart() {

        crawlingService.kCrawlingStart();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 로컬 파일로 저장.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/local")
    public ResponseEntity<Void> csvFileToLocal() {

        crawlingService.csvFileToLocal();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 로컬 파일로 DB 업데이트.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/local/update")
    public ResponseEntity<Void> localToDataBase() {

        crawlingService.localToDataBase();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Content 단건 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ContentResponseDto> getOneContent(@RequestParam String contentUrl) {

        String decodeUrl = URLDecoder.decode(contentUrl, StandardCharsets.UTF_8);

        ContentResponseDto responseDto = crawlingService.getOneContent(decodeUrl);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
