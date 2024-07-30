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

//        mNovelService.mNovelsStart(); // 아직 Content 엔티티와 연결 안됨.
//        kNovelService.kNovelsStart(); // 아직 Content 엔티티와 연결 안됨.
//        kComicsService.kComicsStart();
//        rNovelService.rNovelsStart(); // 아직 Content 엔티티와 연결 안됨.
        // CSV 파일 변환하여 로컬에 저장 기능
//        crawlingUtil.exportToCsv();
        // 로컬 CSV 파일 조회하여 DB 업데이트. 수정중.
//        crawlingUtil.updateDatabase();

    }

}
