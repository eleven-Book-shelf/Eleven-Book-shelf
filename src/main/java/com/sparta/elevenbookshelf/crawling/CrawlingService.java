package com.sparta.elevenbookshelf.crawling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingService {

    private final WebDriver webDriver;

    public void performGoogleSearch() {
        webDriver.get(""); // "" 안에 크롤링할 페이지 URL 넣기
        log.info("performGoogleSearch 메서드 실행. 크롤링할 페이지 : " + webDriver.getCurrentUrl());
        log.info("크롤링한 페이지의 제목 : " + webDriver.getTitle());

        webDriver.quit();
    }

}
