package com.sparta.elevenbookshelf.crawling.Service;

import com.sparta.elevenbookshelf.crawling.CrawlingUtil;
import com.sparta.elevenbookshelf.dto.ContentDataResponseDto;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingService {

    private final ContentRepository contentRepository;
    private final MNovelService mNovelService;
    private final KPageService kPageService;
    private final RNovelService rNovelService;
    private final CrawlingUtil crawlingUtil;

    // 크롤링 데이터를 DB에 저장
    public void allCrawlingStart() {

        rNovelService.serviceStart();
        mNovelService.serviceStart();
        kPageService.serviceStart();

    }

    // 파일 변환 후 로컬에 저장.
    public void csvFileToLocal() {

        crawlingUtil.exportToCsv();

    }

    // 로컬에 저장된 파일로 DB 업데이트 하기.
    public void localToDataBase() {

        crawlingUtil.updateDatabase();

    }

    // content 테이블에 저장된 작품 URL 을 기준으로 해당하는 작품을 검색
    public ContentDataResponseDto getOneContent(String contentUrl) {
        Content content = contentRepository.findByUrl(contentUrl).orElseThrow(()
                -> new NotFoundException("URL 에 해당하는 작품이 없습니다. 입력받은 URL : " + contentUrl));

        return new ContentDataResponseDto(content);
    }

}
