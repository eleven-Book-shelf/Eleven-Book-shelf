package com.sparta.elevenbookshelf.crawling;

import com.sparta.elevenbookshelf.crawling.Service.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final MNovelService mNovelService;
    private final KComicsService kComicsService;
    private final KNovelService kNovelService;
    private final RNovelService rNovelService;
    private final CrawlingUtil crawlingUtil;

    @PostConstruct
    public void startCrawling() {

//        mNovelService.mNovelsStart();
//        kNovelService.kNovelsStart();
//        kComicsService.kComicsStart();
//        rNovelService.rNovelsStart();

//        crawlingUtil.exportToCsv();
        crawlingUtil.updateDatabase();

    }

}
