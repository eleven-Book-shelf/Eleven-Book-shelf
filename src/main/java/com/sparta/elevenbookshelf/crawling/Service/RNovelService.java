package com.sparta.elevenbookshelf.crawling.Service;

import com.sparta.elevenbookshelf.crawling.CrawlingTest;
import com.sparta.elevenbookshelf.crawling.CrawlingTestRepository;
import com.sparta.elevenbookshelf.crawling.CrawlingUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "RService")
@EnableScheduling
public class RNovelService {

    private final WebDriver webDriver;
    private final CrawlingTestRepository crawlingTestRepository;
    private final CrawlingUtil crawlingUtil;
    private final Set<String> disAllowedLink = new HashSet<>();

    @Value("${R_PAGE}")
    private String rPage;

    @Value("${R_ART_LINK}")
    private String rArtLink;

    @Value("${R_AUTHOR}")
    private String rAuthor;

    @Value("${R_CONTENTTYPE}")
    private String rContentType;

    @Value("${R_LIKECOUNT}")
    private String rLikeCount;

    @Value("${R_RATING}")
    private String rRating;

    @Value("${HEADER_ART_TITLE}")
    private String rArtTitle;

    @Value("${HEADER_SITE_NAME}")
    private String rSiteName;

    @Value("${R_COMPLETE}")
    private String rCompleteOrNot;

    @Value("${R_NEXT_BUTTON}")
    private String rNextButton;

    @PostConstruct
    public void init() {
        doNotEnterThisLink();
        rPageStart();
    }

    public void rPageStart() {
        log.info("R NOVEL 시작");
        String baseUrl = rPage;
        int page = 1;

        try {
            while (true) {
                webDriver.get(baseUrl + page);
                log.info("크롤링 할 페이지 : {}", webDriver.getCurrentUrl());
                crawlingUtil.waitForPage();
//                crawlingUtil.scrollController();

                // 중복을 제거하기 위해 Set 사용
                Set<String> uniqueLinks = new HashSet<>();
                List<WebElement> linkElements;

                // 페이지 내의 모든 링크를 반복해서 찾고 중복 제거
                linkElements = crawlingUtil.waitForElements(By.cssSelector(rArtLink), 10);
                for (WebElement element : linkElements) {
                    String uniqueLink = element.getAttribute("href");
                    uniqueLinks.add(uniqueLink);
                }

                log.info("유일한 링크 개수 {}: ", uniqueLinks.size());
                for (String unique : uniqueLinks) {
                    log.info("가져온 링크 : {}", unique);
                }

                int totalLinks = uniqueLinks.size();
                int index = 0;
                for (String artUrl : uniqueLinks) {
                    CrawlingTest crawlingTest = new CrawlingTest();
                    log.info("현재 링크 위치 : {}/{}", ++index, totalLinks);

                    try {
                        log.info("작품 정보 URL : {}", artUrl);
                        crawlingUtil.waitForPage();
                        webDriver.get(artUrl);

                        // robots.txt 규약 위반시 이전 페이지로 이동.
                        if (crawlingUtil.checkTheLink(artUrl, disAllowedLink)) {
                            log.error("!!!robots.txt 규약 위반!!!! {}", artUrl);
                            webDriver.navigate().back();
                            continue;
                        }

                        crawlingUtil.waitForPage();
                        webDriver.get(artUrl);
                        log.info("찾은 링크로 이동 {}: ", artUrl);

                        crawlingUtil.waitForPage();
                        String title = crawlingUtil.metaData(rArtTitle, "content");
                        crawlingTest.setTitle(title);
                        log.info("작품 제목 : {}", title);

                        crawlingUtil.waitForPage();
                        String author = crawlingUtil.bodyData(rAuthor);
                        crawlingTest.setAuthor(author);
                        log.info("작가 : {}", author);

                        crawlingUtil.waitForPage();
                        String site = crawlingUtil.metaData(rSiteName, "content");
                        crawlingTest.setPlatform(site);
                        log.info("작품 게시 사이트 : {}", site);

                        crawlingUtil.waitForPage();
                        String completeOrNot = crawlingUtil.bodyData(rCompleteOrNot);
                        crawlingTest.setCompleteOrNot(completeOrNot);
                        log.info("완결 여부 : {}", completeOrNot);

                        crawlingUtil.waitForPage();
                        String contentType = crawlingUtil.bodyData(rContentType);
                        String contentTypeText = contentType.replace("웹툰", "웹소설");
                        crawlingTest.setContentType(contentTypeText);
                        log.info("장르 : {}", contentTypeText);

                        crawlingUtil.waitForPage();
                        String likeCountText = crawlingUtil.bodyData(rLikeCount);
                        String likeCountNumber = likeCountText.replace(",", "");
                        Long likeCount = Long.parseLong(likeCountNumber);
                        crawlingTest.setLikeCount(likeCount);
                        log.info("좋아요 수 : {}", likeCount);

                        try {
                            //TODO : 0명 이라는 텍스트 데이터도 있음.
                            crawlingUtil.waitForPage();
                            String ratingData = crawlingUtil.bodyData(rRating);
                            String numberOnly = ratingData.replaceAll("점|명","").trim();
                            Double rating = Double.parseDouble(numberOnly);
                            crawlingTest.setRating(rating);
                            log.info("별점 : {}", rating);

                        } catch (NoSuchElementException | TimeoutException e) {
                            crawlingTest.setRating(0.0);
                            log.error("별점 : 표본 부족으로 0.0 저장.");

                        }

                        crawlingUtil.waitForPage();
                        String type = webDriver.getTitle();
                        if (type.contains("웹툰")) {
                            log.info("작품 타입 : 웹툰");
                            crawlingTest.setComicsOrBook("웹툰");
                        } else {
                            log.info("작품 타입 : 웹소설");
                            crawlingTest.setComicsOrBook("웹소설");
                        }

                        crawlingTestRepository.save(crawlingTest);

                        crawlingUtil.waitForPage();
                        webDriver.navigate().back();
                        log.info("이전 페이지로 돌아갑니다.");
                        log.info("===================================");

                        // 하나의 데이터를 얻어온 후 2초간 쓰레드 정지. (표적 사이트 서버의 부하를 줄이기 위한 조치.)
                        crawlingUtil.sleep(2000);

                    } catch (NoSuchElementException e) {
                        log.error("catch : 요소를 찾을 수 없습니다: {}", e.getMessage(), e);
                        log.error("페이지 소스 : {}", webDriver.getPageSource());

                    } catch (TimeoutException e) {
                        // 페이지 리소스 파일 검사하여 로그인 페이지를 넘기는 메서드가 동작하지 않기에 타임아웃 발생 = 로그인 페이지 로 가정하고 다음 반복으로 넘어가게끔 설정.
                        log.error("catch : 요소를 찾는 시간 초과: {}", e.getMessage(), e);
                        webDriver.navigate().back();
                        crawlingUtil.waitForPage();

                    } catch (Exception e) {
                        log.error("catch : 링크 처리중 오류 발생 : {}", e.getMessage(), e);
                        webDriver.navigate().back();
                        crawlingUtil.waitForPage();

                    }
                }

                // 다음 페이지로 이동
                try {
//                    WebElement nextPageButton = webDriver.findElement(By.cssSelector("a[href*='/category/books/1750?page=" + (page + 1) + "']"));
                    WebElement nextPageButton = webDriver.findElement(By.cssSelector(rNextButton.replace("{page}", String.valueOf(page + 1))));
                    nextPageButton.click();
                    page++;
                } catch (NoSuchElementException e) {
                    log.info("더 이상 페이지가 없습니다.");
                    break;
                }
            }

        } finally {
            webDriver.quit();
            log.info("리디 크롤링 종료");
            log.info("========================");
        }
    }

    @PostConstruct
    public void doNotEnterThisLink() {
        disAllowedLink.add("/payment/");
        disAllowedLink.add("/api/");
        disAllowedLink.add("/support/notice");

        for (String disAllowed : disAllowedLink) {
            log.info("접근금지 링크 : {}", disAllowed);
        }

    }
}
