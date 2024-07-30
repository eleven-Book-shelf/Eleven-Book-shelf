package com.sparta.elevenbookshelf.crawling;

import com.sparta.elevenbookshelf.crawling.Service.KPageService;
import com.sparta.elevenbookshelf.crawling.Service.MNovelService;
import com.sparta.elevenbookshelf.crawling.Service.RNovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
public class CrawlingController {

    private final MNovelService mNovelService;
    private final KPageService kPageService;
    private final RNovelService rNovelService;
    private final CrawlingUtil crawlingUtil;

    @PostMapping("/start/all")
    public ResponseEntity<Void> allCrawlingStart() {

        mNovelService.mNovelsStart();
        kPageService.serviceStart();
        rNovelService.rNovelsStart();

        crawlingUtil.exportToCsv();

        crawlingUtil.updateDatabase();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/local")
    public ResponseEntity<Void> csvFileToLocal() {
        crawlingUtil.exportToCsv();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/local/update")
    public ResponseEntity<Void> localToDataBase() {
        crawlingUtil.updateDatabase();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
