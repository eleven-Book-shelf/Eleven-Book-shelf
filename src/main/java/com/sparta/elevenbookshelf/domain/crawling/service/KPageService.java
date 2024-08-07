package com.sparta.elevenbookshelf.domain.crawling.service;

import com.sparta.elevenbookshelf.common.crawling.CrawlingUtil;
import com.sparta.elevenbookshelf.domain.content.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
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

    // K Novel 과 comics 를 지정해서 작동하는 메서드.
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

        // 웹드라이버의 get 메서드를 사용하여 지정한 페이지로 이동.
        webDriver.get(page);
        log.info("크롤링 할 페이지 : {}", webDriver.getCurrentUrl());

        try {
            crawlingUtil.waitForPage();
            crawlingUtil.scrollToEndOfPage();

            // cssSelector 를 사용하여 해당
            List<WebElement> linkElements = crawlingUtil.waitForElements(By.cssSelector(pageArtLink), 10);
            log.info("찾은 링크 개수 {}: ", linkElements.size());

            // 만약 링크를 찾아오지 못한다면 링크 없음 반환한 후 끝내기.
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

            // 반복문을 사용해 각 작품마다 크롤링 시작.
            for (int i = 0; i < allLinks.size(); i++) {
                ContentRequestDto requestDto = new ContentRequestDto();

                // allLinks에 저장되어 있는 링크들을 하나씩 가져오기 위해 i로 설정. i가 늘어날때마다 다음 링크로 넘아감.
                String artUrl = allLinks.get(i);
                requestDto.setUrl(artUrl);
                log.info("현재 링크 위치 : {}/{}", i + 1, allLinks.size());

                try {
                    // robots.txt 규약 위반시 이전 페이지로 이동.
                    if (crawlingUtil.checkTheLink(artUrl, disAllowedLink)) {
                        log.error("!!!robots.txt 규약 위반!!!! {}", artUrl);
                        webDriver.navigate().back();
                        continue;
                    }

                    // 위와 동일하게 WebDriver 의 get() 을 사용하여 작품 URL 로 접속
                    crawlingUtil.waitForPage();
                    webDriver.get(artUrl);
                    log.info("작품정보 URL : {}", artUrl);
                    log.info("찾은 링크로 이동 {}: ", artUrl);

                    // 작품 제목
                    crawlingUtil.waitForPage();
                    String title = crawlingUtil.metaData(kTitle, "content");
                    requestDto.setTitle(title);
                    log.info("작품 제목 : {}", title);

                    // 작품 작가
                    crawlingUtil.waitForPage();
                    String author = crawlingUtil.metaData(kAuthor, "content");
                    requestDto.setAuthor(author);
                    log.info("작가 : {}", author);

                    // 작품이 게시된 사이트
                    crawlingUtil.waitForPage();
                    String site = crawlingUtil.metaData(kSite, "content");
                    requestDto.setPlatform(site);
                    log.info("작품 게시 사이트 : {}", site);

                    // 완결 유무 체크. metaData 에서 지정된 위치에 연재 가 적혀있는지 체크.
                    crawlingUtil.waitForPage();
                    String completeOrNot = crawlingUtil.metaData(kComplete, "content");
                    if (completeOrNot.contains("연재")) {
                        requestDto.setIsEnd(Content.ContentEnd.NOT);
                        log.info("완결 유무 : NOT");
                    } else {
                        requestDto.setIsEnd(Content.ContentEnd.END);
                        log.info("완결 유무 : END");
                    }

                    // 작품 소개
                    crawlingUtil.waitForPage();
                    String description = crawlingUtil.metaData("//meta[@name='description']", "content");
                    requestDto.setDescription(description);
                    log.info("작품 소개. : {}", description);

                    // 총 조회수
                    crawlingUtil.waitForPage();
                    String totalViewData = crawlingUtil.bodyData(kTotalView);
                    totalViewData = totalViewData.replace(",", "").trim();

                    // 조회수 데이터에 '만' 이라는 텍스트가 섞여 있을 경우
                    if (totalViewData.contains("만")) {
                        String totalViewParse = totalViewData.replace("만", "").trim();
                        Double totalView = Double.parseDouble(totalViewParse) * 10000;
                        requestDto.setView(totalView);
                        log.info("만 제거한 총 조회수 : {}", totalView);

                    } else if (totalViewData.contains("억")) {
                        // 조회수 데이터에 '억' 이라는 텍스트가 섞여 있을 경우
                        String totalViewParse = totalViewData.replace("억","").trim();
                        Double totalView = Double.parseDouble(totalViewParse) * 100000000;
                        requestDto.setView(totalView);
                        log.info("억 제거한 총 조회수 {}", totalView);

                    } else {
                        // '만'이나 '억'이 하나도 없는 순수 숫자형 텍스트일 경우
                        Double totalView = Double.parseDouble(totalViewData);
                        requestDto.setView(totalView);
                        log.info("조회수 : {}", totalView);
                    }

                    // 작품 장르
                    crawlingUtil.waitForPage();
                    String genre = crawlingUtil.bodyData(kContentType);
                    requestDto.setGenre(genre);
                    log.info("장르 : {}", genre);

                    // 작품 별점
                    try {
                        crawlingUtil.waitForPage();
                        String ratingData = crawlingUtil.bodyData(kRating);
                        Double rating = Double.parseDouble(ratingData);
                        requestDto.setRating(rating);
                        log.info("별점 : {}", rating);

                    } catch (NoSuchElementException | TimeoutException e) {
                        // 최신작이거나 인기가 없어 별점이 하나도 등록되지 않은 작품은 별점 데이터 자체가 페이지에 빠져있는 경우가 있음.
                        // 그렇기에 요소를 찾지못하거나 타임아웃이 발생할 경우 예외처리하여 0.0점 저장
                        requestDto.setRating(0.0);
                        log.error("별점 : 표본 부족으로 0.0 저장.");

                    }

                    // 작품 타입.
                    crawlingUtil.waitForPage();
                    String type = webDriver.getTitle();
                    if (type.contains("웹툰")) {
                        requestDto.setType(Content.ContentType.COMICS);
                        log.info("작품 타입 : 웹툰");
                    } else {
                        requestDto.setType(Content.ContentType.NOVEL);
                        log.info("작품 타입 : 웹소설");
                    }

                    // 썸네일
                    String imgUrlXpath = String.format("//img[@alt='%s']", "썸네일");
                    String imgUrl = crawlingUtil.getThumbnail(imgUrlXpath, false);
                    requestDto.setImgUrl(imgUrl);
                    log.info("작품 썸네일 : {}", imgUrl);

                    // 해시태그
                    try {
                        // 해시테그는 작품 안에서 정보란을 눌러야 나오기에 해당 URL 로 이동
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

                    // 현재 페이지에 북마크 카운트는 존재하지 않음
                    requestDto.setBookMarkCount(0L);
                    log.info("북마크 카운트 없음 : 0");

                    // 마찬가지로 현재 페이지에 좋아요 수 없음
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
