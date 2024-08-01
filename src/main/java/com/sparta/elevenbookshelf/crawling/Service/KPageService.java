package com.sparta.elevenbookshelf.crawling.Service;

import com.sparta.elevenbookshelf.crawling.CrawlingUtil;
import com.sparta.elevenbookshelf.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.entity.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "KPageService")
public class KPageService {

    private final WebDriver webDriver;
    private final Set<String> disAllowedLink = new HashSet<>();
    private final CrawlingUtil crawlingUtil;

    @Value("${k.page.novel}")
    private String kNovelPage;

    @Value("${k.page.comics}")
    private String kComicsPage;

    @Value("${k.robots1}")
    private String robotsTxtNo1;

    @Value("${k.robots2}")
    private String robotsTxtNo2;

    @Value("${k.art_link}")
    private String pageArtLink;

    @Value("${k.title}")
    private String kTitle;

    @Value("${k.author}")
    private String kAuthor;

    @Value("${k.site}")
    private String kSite;

    @Value("${k.complete}")
    private String kComplete;

    @Value("${k.total_view}")
    private String kTotalView;

    @Value("${k.content_type}")
    private String kContentType;

    @Value("${k.rating}")
    private String kRating;

    @Value("${k.hashtag}")
    private String kHashTag;

    public void serviceStart() {

        log.info("K PAGE 시작.");

        kPage(kNovelPage, "K NOVEL");
        log.info("K NOVEL 종료.");

        kPage(kComicsPage, "K COMICS");
        log.info("K COMICS 종료. K PAGE 끝.");

    }

    // 크롤링 메서드
    private void kPage(String page, String pageType) {
        doNotEnterThisLink();
        log.info("{} 시작.", pageType);
        webDriver.get(page);
        log.info("크롤링 할 페이지 : {}", webDriver.getCurrentUrl());

        try {
            crawlingUtil.waitForPage();
//            crawlingUtil.scrollToEndOfPage();

            List<WebElement> linkElements = crawlingUtil.waitForElements(By.cssSelector(pageArtLink), 10);
            log.info("찾은 링크 개수 {}: ", linkElements.size());

            if (linkElements.isEmpty()) {
                log.error("링크가 없습니다.");
                return;
            }

            // 모든 작품 링크를 수집하여 별도의 리스트에 저장
            List<String> allLinks = new ArrayList<>();
            for (WebElement linkElement : linkElements) {
                String artUrl = linkElement.getAttribute("href");
                if (!crawlingUtil.checkTheLink(artUrl, disAllowedLink)) {
                    log.info("저장된 작품 링크 : {}", artUrl);
                    allLinks.add(artUrl);
                }
            }

            int totalLinks = allLinks.size();

            for (int i = 0; i < totalLinks; i++) {
                ContentRequestDto requestDto = new ContentRequestDto();

                String artUrl = allLinks.get(i);
                requestDto.setUrl(artUrl);
                log.info("현재 링크 위치 : {}/{}", i + 1, totalLinks);

                try {
                    log.info("작품정보 URL : {}", artUrl);
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
                    String title = crawlingUtil.metaData(kTitle, "content");
                    requestDto.setTitle(title);
                    log.info("작품 제목 : {}", title);

                    crawlingUtil.waitForPage();
                    String author = crawlingUtil.metaData(kAuthor, "content");
                    requestDto.setAuthor(author);
                    log.info("작가 : {}", author);

                    crawlingUtil.waitForPage();
                    String site = crawlingUtil.metaData(kSite, "content");
                    requestDto.setPlatform(site);
                    log.info("작품 게시 사이트 : {}", site);

                    crawlingUtil.waitForPage();
                    String completeOrNot = crawlingUtil.metaData(kComplete, "content");
                    if (completeOrNot.contains("연재")) {
                        requestDto.setIsEnd(Content.ContentEnd.NOT);
                        log.info("완결 유무 : NOT");
                    } else {
                        requestDto.setIsEnd(Content.ContentEnd.END);
                        log.info("완결 유무 : END");
                    }

                    crawlingUtil.waitForPage();
                    String description = crawlingUtil.metaData("//meta[@name='description']", "content");
                    requestDto.setDescription(description);
                    log.info("작품 소개. : {}", description);

                    crawlingUtil.waitForPage();
                    String totalViewData = crawlingUtil.bodyData(kTotalView);
                    totalViewData = totalViewData.replace(",", "").trim();

                    if (totalViewData.contains("만")) {
                        String totalViewParse = totalViewData.replace("만", "").trim();
                        Double totalView = Double.parseDouble(totalViewParse) * 1000;

                        requestDto.setView(totalView);
                        log.info("만 제거한 총 조회수 : {}", totalView);

                    } else {
                        Double totalView = Double.parseDouble(totalViewData);
                        requestDto.setView(totalView);
                        log.info("조회수 : {}", totalView);
                    }

                    crawlingUtil.waitForPage();
                    String genre = crawlingUtil.bodyData(kContentType);
                    requestDto.setGenre(genre);
                    log.info("장르 : {}", genre);

                    try {
                        crawlingUtil.waitForPage();
                        String ratingData = crawlingUtil.bodyData(kRating);
                        Double rating = Double.parseDouble(ratingData);
                        requestDto.setRating(rating);
                        log.info("별점 : {}", rating);

                    } catch (NoSuchElementException | TimeoutException e) {
                        requestDto.setRating(0.0);
                        log.error("별점 : 표본 부족으로 0.0 저장.");

                    }

                    crawlingUtil.waitForPage();
                    String type = webDriver.getTitle();
                    if (type.contains("웹툰")) {
                        requestDto.setType(Content.ContentType.COMICS);
                        log.info("작품 타입 : 웹툰");
                    } else {
                        requestDto.setType(Content.ContentType.NOVEL);
                        log.info("작품 타입 : 웹소설");
                    }

                    String imgUrlXpath = String.format("//img[@alt='%s']", "썸네일");
                    String imgUrl = crawlingUtil.getThumbnail(imgUrlXpath, false);
                    requestDto.setImgUrl(imgUrl);
                    log.info("작품 썸네일 : {}", imgUrl);

                    try {
                        String hashTagUrl = artUrl + "?tab_type=about";
                        webDriver.get(hashTagUrl);
                        log.info("이동한 페이지 : {}", hashTagUrl);
                        List<String> hashTagList = crawlingUtil.getHashtags(kHashTag);
                        String joinHashTags = String.join(",", hashTagList);
                        requestDto.setContentHashTag(joinHashTags);
                        log.info("해시태그 : {}", joinHashTags);


                    } catch (NoSuchElementException e) {
                        log.error("해시태그를 찾을 수 없습니다: {}", e.getMessage());
                        requestDto.setContentHashTag("없음");
                    } catch (TimeoutException e) {
                        log.error("해시태그를 찾는 시간 초과. {}", e.getMessage());
                        requestDto.setContentHashTag("없음");
                    } catch (Exception e) {
                        log.error("해시태그를 찾는 중 에러 발생. {}", e.getMessage());
                        requestDto.setContentHashTag("없음");
                    }

                    requestDto.setBookMarkCount(0L);
                    log.info("북마크 카운트 없음 : 0");

                    requestDto.setLikeCount(0L);
                    log.info("좋아요 카운트 없음 : 0");

                    crawlingUtil.saveData(requestDto, artUrl);

                    crawlingUtil.waitForPage();
                    webDriver.navigate().back();
                    log.info("이전 페이지로 돌아갑니다.");
                    log.info("===================================");

                    // 하나의 데이터를 얻어온 후 2초간 쓰레드 정지. (표적 사이트 서버의 부하를 줄이기 위한 조치.)
                    crawlingUtil.sleep(2000);

                } catch (NoSuchElementException e) {
                    log.error("요소를 찾을 수 없습니다: {}", e.getMessage(), e);
                    log.error("페이지 소스 : {}", webDriver.getPageSource());

                } catch (TimeoutException e) {
                    // 페이지 리소스 파일 검사하여 로그인 페이지를 넘기는 메서드가 동작하지 않기에 타임아웃 발생 = 로그인 페이지 로 가정하고 다음 반복으로 넘어가게끔 설정.
                    log.error("요소를 찾는 시간 초과: {}", e.getMessage(), e);
                    webDriver.navigate().back();
                    crawlingUtil.waitForPage();

                } catch (Exception e) {
                    log.error("catch : 링크 처리중 오류 발생 : {}", e.getMessage(), e);
                    webDriver.navigate().back();
                    crawlingUtil.waitForPage();

                }

            }

        } finally {
            log.info("크롤링 종료");
            log.info("=============================");
        }

    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    // robots.txt 파일에 규정된 접근 금지 목록.
    // TODO : 사이트별 robots.txt 규약을 적응형으로 적용하게끔 수정 필요.
    private void doNotEnterThisLink() {
        disAllowedLink.add(robotsTxtNo1);
        disAllowedLink.add(robotsTxtNo2);

        for (String disAllowed : disAllowedLink) {
            log.info("접근 금지 링크 : {}", disAllowed);
        }

    }
}
