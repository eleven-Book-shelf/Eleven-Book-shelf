package com.sparta.elevenbookshelf.crawling;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingService {

    private final WebDriver webDriver;

    @Value("${CRAWLING_PAGE}")
    private String kCrawlingPage;

    @Value("${K_ROBOTS_TXT1}")
    private String robotsTxtNo1;

    @Value("${K_ROBOTS_TXT2}")
    private String robotsTxtNo2;

    @Value("${CRAWLING_ART_LINK}")
    private String pageArtLink;

    @Value("${COMICS_ART_TITLE}")
    private String artTitle;

    private Set<String> disAllowedLink;

    // robots.txt 파일에 규정된 접근 금지 목록.
    // TODO : 사이트별 robots.txt 규약을 적응형으로 적용하게끔 수정 필요.
    @PostConstruct
    public void doNotEnterThisLink() {
        disAllowedLink = new HashSet<>();
        disAllowedLink.add(robotsTxtNo1);
        disAllowedLink.add(robotsTxtNo2);
        log.info("robots.txt : " + disAllowedLink);
    }

    // robots.txt 규약 준수를 위한 URL 검사 메서드.
    private boolean checkTheLink(String url) {
        for (String disallowedPath : disAllowedLink) {
            if (url.contains(disallowedPath)) {
                return true;
            }
        }
        return false;
    }

    // 로그인이 필요한 페이지인지 체크.
    // TODO : 현재 동작 X. css 요소를 로드하는게 느리거나 요소를 아예 찾지 못하는 상황 발생중.
    private boolean checkTheLoginPage() {
        String pageSource = webDriver.getPageSource();
        log.info("페이지 소스 : {}", pageSource);
        if (pageSource.contains("로그인")) {
            log.warn("로그인이 필요한 페이지임이 확인됨");
            return true;
        }
        return false;
    }

    public void performGoogleSearch() {

        log.info("크롤링 시작.");
        webDriver.get(kCrawlingPage);
        log.info("크롤링 할 페이지 : " + webDriver.getCurrentUrl());

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        try {
            while (true) {
                wait.until(webDriver
                        -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete"));

                List<WebElement> linkElements = wait.until(ExpectedConditions
                        .presenceOfAllElementsLocatedBy(By.cssSelector(pageArtLink)));
                log.info("찾은 링크 개수 {}: ", linkElements.size());

                //
                if (linkElements.isEmpty()) {
                    break;
                }

                for (int i = 0; i < linkElements.size(); i++) {
                    try {
                        // cssSelector 사용. 작품 링크 서치.
                        // TODO : 현재 표적 사이트가 스크롤을 계속할시 생성되는 사이트라 스크롤 전까지만 링크를 찾아냄. 적응형 필요.
                        linkElements = webDriver.findElements(By.cssSelector(pageArtLink));
                        log.info("링크 목록의 크기 : {}", linkElements.size());

                        // 인덱스가 요소보다 클경우
                        if (i >= linkElements.size()) {
                            log.warn("i의 크기가 링크 목록보다 큽니다 : {}", i);
                            continue;
                        }

                        // href 로 정의되어 있는 작품 링크를 찾아서 들어감.
                        String artUrl = linkElements.get(i).getAttribute("href");
                        log.info("작품정보 URL : {}", artUrl);

                        // robots.txt 규약 위반시 이전 페이지로 이동.
                        if (checkTheLink(artUrl)) {
                            log.error("!!!robots.txt 규약 위반!!!! {}", artUrl);
                            webDriver.navigate().back();
                            continue;
                        }

                        webDriver.get(artUrl);
                        log.info("찾은 링크로 이동 {}: ", artUrl);

                        wait.until(webDriver1
                                -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

//                        if (checkTheLoginPage()) {
//                            log.info("로그인이 필요한 페이지입니다. 링크 건너뛰고 다음으로 이동 : {}", artUrl);
//                            webDriver.navigate().back();
//                            wait.until(webDriver
//                                    -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
//                            continue;
//                        }

                        WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(artTitle)));
                        String title = titleElement.getText();
                        log.info("제목 찾기 : {}", title);

                        webDriver.navigate().back();
                        log.info("이전 페이지로 돌아갑니다.");

                        wait.until(webDriver
                                -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

                        Thread.sleep(2000);

                    } catch (NoSuchElementException e) {
                        log.error("요소를 찾을 수 없습니다: {}", e.getMessage(), e);
                        webDriver.quit();

                    } catch (TimeoutException e) {
                        // 페이지 리소스 파일 검사하여 로그인 페이지를 넘기는 메서드가 동작하지 않기에 타임아웃 발생 = 로그인 페이지 로 가정하고 다음 반복으로 넘어가게끔 설정.
                        log.error("요소를 찾는 시간 초과: {}", e.getMessage(), e);
                        webDriver.navigate().back();
                        wait.until(webDriver
                                -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

                    } catch (Exception e) {
                        log.error("catch : 링크 처리중 오류 발생 : {}", e.getMessage(), e);
                        webDriver.navigate().back();
                        wait.until(webDriver
                                -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

                    }

                }

            }

        } finally {
            webDriver.quit();
            log.info("크롤링 종료");
        }

    }

}