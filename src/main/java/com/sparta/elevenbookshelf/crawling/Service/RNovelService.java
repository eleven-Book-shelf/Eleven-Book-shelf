package com.sparta.elevenbookshelf.crawling.Service;

import com.sparta.elevenbookshelf.crawling.CrawlingUtil;
import com.sparta.elevenbookshelf.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.entity.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "RNovelService")
public class RNovelService {

    private final WebDriver webDriver;
    private final CrawlingUtil crawlingUtil;
    private final Set<String> disAllowedLink = new HashSet<>();

    @Value("${R_PAGE}")
    private String rPage;

    @Value("${R_ART_CLASS}")
    private String rArtClass;

    @Value("${R_ART_LINK}")
    private String rArtLink;

    @Value("${R_AUTHOR}")
    private String rAuthor;

    @Value("${R_CONTENT_TYPE}")
    private String rContentType;

    @Value("${R_LIKE_COUNT}")
    private String rLikeCount;

    @Value("${R_RATING}")
    private String rRating;

    @Value("${HEADER_ART_TITLE}")
    private String rArtTitle;

    @Value("${R_SITE_NAME}")
    private String rSiteName;

    @Value("${R_COMPLETE}")
    private String rIsEnd;

    public void serviceStart() {
        log.info("R NOVEL 시작");
        String baseUrl = rPage;
        int page = 1;

        try {
            while (true) {
                doNotEnterThisLink();
                webDriver.get(baseUrl + page);
                log.info("크롤링 할 페이지 : {}", webDriver.getCurrentUrl());
                crawlingUtil.waitForPage();

//                crawlingUtil.scrollController();

                // 중복제거
                Set<String> uniqueLinks = crawlingUtil.notDuplicatedLinks(By.cssSelector(rArtClass), By.cssSelector(rArtLink));

                int index = 0;
                for (String artUrl : uniqueLinks) {
                    ContentRequestDto requestDto = new ContentRequestDto();
                    log.info("현재 링크 위치 : {}/{}", ++index, uniqueLinks.size());

                    try {
                        log.info("작품 정보 URL : {}", artUrl);
                        crawlingUtil.waitForPage();
                        webDriver.get(artUrl);
                        requestDto.setUrl(artUrl);

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
                        requestDto.setTitle(title);
                        log.info("작품 제목 : {}", title);

                        crawlingUtil.waitForPage();
                        String author = crawlingUtil.bodyData(rAuthor);
                        requestDto.setAuthor(author);
                        log.info("작가 : {}", author);

                        crawlingUtil.waitForPage();
                        String site = crawlingUtil.metaData(rSiteName, "content");
                        requestDto.setPlatform(site);
                        log.info("작품 게시 사이트 : {}", site);

                        crawlingUtil.waitForPage();
                        String isEnd = crawlingUtil.bodyData(rIsEnd);
                        if (isEnd.contains("미완결")) {
                            requestDto.setIsEnd(Content.ContentEnd.NOT);
                            log.info("완결 여부 : NOT");
                        } else {
                            requestDto.setIsEnd(Content.ContentEnd.END);
                            log.info("완결 여부 : END");
                        }

                        crawlingUtil.waitForPage();
                        String description = crawlingUtil.metaData("//meta[@name='description']","content");
                        requestDto.setDescription(description);
                        log.info("작품 소개 : {}", description);

                        crawlingUtil.waitForPage();
                        String genreType = crawlingUtil.bodyData(rContentType);
                        String genre = genreType.replace("웹툰", "웹소설");
                        requestDto.setGenre(genre);
                        log.info("장르 : {}", genre);

                        crawlingUtil.waitForPage();
                        String likeCountText = crawlingUtil.bodyData(rLikeCount);
                        String likeCountNumber = likeCountText.replace(",", "");
                        Long likeCount = Long.parseLong(likeCountNumber);
                        requestDto.setLikeCount(likeCount);
                        log.info("좋아요 수 : {}", likeCount);

                        try {
                            crawlingUtil.waitForPage();
                            String ratingData = crawlingUtil.bodyData(rRating);
                            String numberOnly = ratingData.replaceAll("[점명]","").trim();
                            Double rating = Double.parseDouble(numberOnly);
                            requestDto.setRating(rating);
                            log.info("별점 : {}", rating);

                        } catch (NoSuchElementException | TimeoutException e) {
                            requestDto.setRating(0.0);
                            log.error("별점 : 표본 부족으로 0.0 저장.");

                        }

                        crawlingUtil.waitForPage();
                        String type = webDriver.getTitle();
                        if (type.contains("웹툰")) {
                            log.info("작품 타입 : 웹툰");
                            requestDto.setType(Content.ContentType.COMICS);
                        } else {
                            log.info("작품 타입 : 웹소설");
                            requestDto.setType(Content.ContentType.NOVEL);
                        }

                        crawlingUtil.waitForPage();
                        String imgUrl = crawlingUtil.getThumbnail(".thumbnail", true);
                        requestDto.setImgUrl(imgUrl);
                        log.info("작품 썸네일 : {}", imgUrl);

                        requestDto.setView(0.0);
                        requestDto.setBookMark(0L);

                        crawlingUtil.saveData(requestDto, artUrl);

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
                    page++;
                    log.info("다음 페이지로 이동 페이지 : {}", page);

                    if (page == 5) {
                        break;
                    }

                } catch (NoSuchElementException e) {
                    log.info("더 이상 페이지가 없습니다.");
                    break;
                }

            }

        } finally {
            log.info("리디 크롤링 종료");
            log.info("========================");
        }

    }

    public void doNotEnterThisLink() {
        disAllowedLink.add("/payment/");
        disAllowedLink.add("/api/");
        disAllowedLink.add("/support/notice");

        for (String disAllowed : disAllowedLink) {
            log.info("접근금지 링크 : {}", disAllowed);
        }

    }
}
