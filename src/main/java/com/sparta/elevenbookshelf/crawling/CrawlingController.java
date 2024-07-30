package com.sparta.elevenbookshelf.crawling;

import com.sparta.elevenbookshelf.crawling.Service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    private final KComicsService kComicsService;
    private final KNovelService kNovelService;
    private final RNovelService rNovelService;
    private final CrawlingUtil crawlingUtil;

    @PostMapping("/start/all")
    public ResponseEntity<Void> allCrawlingStart() {

        mNovelService.mNovelsStart();
        kComicsService.kComicsStart();
        kNovelService.kNovelsStart();
        rNovelService.rNovelsStart();

        crawlingUtil.exportToCsv();

        crawlingUtil.updateDatabase();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostConstruct
    public void test() {
        mNovelService.mNovelsStart();
//        kComicsService.kComicsStart();
//        kNovelService.kNovelsStart();
//        rNovelService.rNovelsStart();

        crawlingUtil.exportToCsv();

//        crawlingUtil.updateDatabase();
    }

}
