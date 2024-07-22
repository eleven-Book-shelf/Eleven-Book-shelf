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
import java.util.NoSuchElementException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingService")
public class CrawlingService {

    private final WebDriver webDriver;
    private Set<String> disAllowedLink;

    @Value("${CRAWLING_PAGE}")
    private String kCrawlingPage;

    @Value("${K_ROBOTS_TXT1}")
    private String robotsTxtNo1;

    @Value("${K_ROBOTS_TXT2}")
    private String robotsTxtNo2;

    @Value("${CRAWLING_ART_LINK}")
    private String pageArtLink;

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

    // 페이지 로딩을 기다리는 메서드.
    private void waitForPage() {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        wait.until(webDriver
                -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState")
                .equals("complete"));
    }

    // 동적페이지에서 스크롤을 끝까지 내려주는 메서드.
    private void scrollToEndOfPage() {
        // 자바 스크립트 코드를 실행 할 수 있도록 JavascriptExecutor 객체 생성.
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        // 현재 페이지의 총 높이를 저장.
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

        // 스크롤이 바닥에 닿을때까지 반복
        while (true) {
            // 페이지를 끝까지 스크롤함.
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                // 2초동안 쓰레드 중지로 페이지가 로드될 시간 벌기.
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                log.error("쓰레드 슬립 도중 에러 발생 : {}", e.getMessage());
            }

            // 스크롤을 최하단까지 내린후 페이지의 총 높이를 다시 저장.
            long newHeight = (long) js.executeScript("return document.body.scrollHeight");

            // 이번 반복에서 저장한 페이지의 총 높이와 이전 반복에서 저장한 페이지의 총 높이가 같다면 반복을 종료.
            if (newHeight == lastHeight) {
                break;
            }

            // 마지막 반복의 높이값을 계속 업데이트하여 조건문에서 검사가 가능하도록 함.
            lastHeight = newHeight;
        }
    }

    // 복수 : css 로딩을 기다리는 메서드.
    private List<WebElement> waitForElements(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    // 단수 : css 로딩을 기다리는 메서드.
    private WebElement waitForElement(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // 크롤링 메서드
    public void performGoogleSearch() {

        log.info("크롤링 시작.");
        webDriver.get(kCrawlingPage);
        log.info("크롤링 할 페이지 : " + webDriver.getCurrentUrl());

        try {
            waitForPage();
            scrollToEndOfPage();

            List<WebElement> linkElements = waitForElements(By.cssSelector(pageArtLink), 10);
            log.info("찾은 링크 개수 {}: ", linkElements.size());

            if (linkElements.isEmpty()) {
                log.error("링크가 없습니다.");
                return;
            }

            // 모든 링크를 수집하여 별도의 리스트에 저장
            List<String> allLinks = new ArrayList<>();
            for (WebElement linkElement : linkElements) {
                String artUrl = linkElement.getAttribute("href");
                if (!checkTheLink(artUrl)) {
                    log.info("저장된 작품 링크 : {}", artUrl);
                    allLinks.add(artUrl);
                }
            }

            int totalLinks = allLinks.size();
            for (int i = 0; i < totalLinks; i++) {

                String artUrl = allLinks.get(i);
                log.info("현재 링크 위치 : {}/{}", i + 1, totalLinks);

                try {
                    log.info("작품정보 URL : {}", artUrl);
                    waitForPage();
                    webDriver.get(artUrl);

                    // robots.txt 규약 위반시 이전 페이지로 이동.
                    if (checkTheLink(artUrl)) {
                        log.error("!!!robots.txt 규약 위반!!!! {}", artUrl);
                        webDriver.navigate().back();
                        continue;
                    }

                    waitForPage();
                    webDriver.get(artUrl);
                    log.info("찾은 링크로 이동 {}: ", artUrl);

                    waitForPage();
                    WebElement authorElement = waitForElement(By.xpath("//meta[@property='og:title']"), 10);
                    String title = authorElement.getAttribute("content");
                    log.info("작품 제목 : {}", title);

                    waitForPage();
                    WebElement titleElement = waitForElement(By.xpath("//meta[@name='author']"), 10);
                    String author = titleElement.getAttribute("content");
                    log.info("작가 : {}", author);

                    waitForPage();
                    WebElement siteElement = waitForElement(By.xpath("//meta[@property='og:site_name']"), 10);
                    String site = siteElement.getAttribute("content");
                    log.info("작품 게시 사이트 : {}", site);

                    waitForPage();
                    String type = webDriver.getTitle();
                    if (type.contains("웹툰")) {
                        log.info("작품 타입 : 웹툰");
                    } else {
                        log.info("작품 타입 : 웹소설");
                    }

                    waitForPage();
                    WebElement element = waitForElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div/div[2]/a/div/div[1]/div[2]/span"), 10);
                    String RaRaTing = element.getText();
                    log.info("별점 : " + RaRaTing);

                    webDriver.navigate().back();
                    log.info("이전 페이지로 돌아갑니다.");
                    waitForPage();

                    // 하나의 데이터를 얻어온 후 2초간 쓰레드 정지. (표적 사이트 서버의 부하를 줄이기 위한 조치.)
                    Thread.sleep(2000);

                } catch (NoSuchElementException e) {
                    log.error("요소를 찾을 수 없습니다: {}", e.getMessage(), e);
                    log.error("페이지 소스 : {}", webDriver.getPageSource());

                } catch (TimeoutException e) {
                    // 페이지 리소스 파일 검사하여 로그인 페이지를 넘기는 메서드가 동작하지 않기에 타임아웃 발생 = 로그인 페이지 로 가정하고 다음 반복으로 넘어가게끔 설정.
                    log.error("요소를 찾는 시간 초과: {}", e.getMessage(), e);
                    webDriver.navigate().back();
                    waitForPage();

                } catch (Exception e) {
                    log.error("catch : 링크 처리중 오류 발생 : {}", e.getMessage(), e);
                    webDriver.navigate().back();
                    waitForPage();

                }

            }

        } finally {
            webDriver.quit();
            log.info("크롤링 종료");
        }

    }

}