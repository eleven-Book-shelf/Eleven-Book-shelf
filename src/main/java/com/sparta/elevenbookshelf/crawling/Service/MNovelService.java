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
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "MNovelService")
public class MNovelService {

    private final WebDriver webDriver;
    private final CrawlingUtil crawlingUtil;
    private final Set<String> disAllowedLink = new HashSet<>();

    @Value("${M_PAGE}")
    private String mPage;

    @Value("${M_ART_LINK}")
    private String mArtLink;

    @Value("${HEADER_ART_TITLE}")
    private String mArtTitle;

    @Value("${M_AUTHOR}")
    private String mAuthor;

    @Value("${HEADER_SITE_NAME}")
    private String mSite;

    @Value("${M_CONTENT_TYPE}")
    private String mContentType;

    @Value("${M_LIKE_COUNT}")
    private String mLikeCount;

    @Value("${M_BOOK_MARK}")
    private String mBookMark;

    @Value("${M_TOTAL_VIEW}")
    private String mTotalCount;

    @Value("${M_HASHTAG}")
    private String mHashTag;

    public void serviceStart() {
        doNotEnterThisLink();
        log.info("M NOVEL 시작");

        try {
            webDriver.get(mPage);
            log.info("크롤링 할 페이지 : {}", webDriver.getCurrentUrl());
            crawlingUtil.waitForPage();

            Set<String> uniqueLinks = crawlingUtil.notDuplicatedLinks(By.id("SECTION-LIST"),By.cssSelector(mArtLink));

            int index = 0;
            for (String artUrl : uniqueLinks) {
                ContentRequestDto requestDto = new ContentRequestDto();
                requestDto.setUrl(artUrl);
                log.info("현재 링크 위치 : {}/{}", ++index, uniqueLinks.size());

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
                    String title = crawlingUtil.metaData(mArtTitle, "content");
                    requestDto.setTitle(title);
                    log.info("작품 제목 : {}", title);

                    crawlingUtil.waitForPage();
                    String author = crawlingUtil.bodyData(mAuthor);
                    requestDto.setAuthor(author);
                    log.info("작가 : {}", author);

                    crawlingUtil.waitForPage();
                    String site = crawlingUtil.metaData(mSite, "content");
                    requestDto.setPlatform(site);
                    log.info("작품 게시 사이트 : {}", site);

                    // 해당 링크는 연재중인 작품만 모아둔 곳임.
                    requestDto.setIsEnd(Content.ContentEnd.NOT);
                    log.info("완결 여부 : NOT");

                    crawlingUtil.waitForPage();
                    String description = crawlingUtil.metaData("//meta[@name='description']","content");
                    requestDto.setDescription(description);
                    log.info("작품 소개. : {}", description);

                    crawlingUtil.waitForPage();
                    String genre = crawlingUtil.bodyData(mContentType);
                    requestDto.setGenre(genre);
                    log.info("장르 : {}", genre);

                    crawlingUtil.waitForPage();
                    String likeCountText = crawlingUtil.bodyData(mLikeCount);
                    String likeCountNumber = likeCountText.replace(",", "");
                    Long likeCount = Long.parseLong(likeCountNumber);
                    requestDto.setLikeCount(likeCount);
                    log.info("좋아요 수 : {}", likeCount);

                    crawlingUtil.waitForPage();
                    String bookMarkData = crawlingUtil.bodyData(mBookMark);
                    String bookMarkNumber = bookMarkData.replace(",", "");
                    Long bookMark = Long.parseLong(bookMarkNumber);
                    requestDto.setBookMarkCount(bookMark);
                    log.info("북마크 수 : {}", bookMark);

                    crawlingUtil.waitForPage();
                    String totalViewData = crawlingUtil.bodyData(mTotalCount);
                    totalViewData = totalViewData.replace(",", "");
                    Double totalView = Double.parseDouble(totalViewData);
                    requestDto.setView(totalView);
                    log.info("총 조회수 : {}", totalView);

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
                    String imgUrlClass = String.format("img.%s", "cover");
                    String imgUrl = crawlingUtil.getThumbnail(imgUrlClass, true);
                    requestDto.setImgUrl(imgUrl);
                    log.info("작품 썸네일 : {}", imgUrl);

                    try {
                        List<String> hashTags = crawlingUtil.getHashtags(mHashTag);
                        String hashTag = String.join(",", hashTags);
                        requestDto.setContentHashTag(hashTag);
                        log.info("해시태그 : {}", hashTag);

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

                    requestDto.setRating(0.0);
                    log.info("현재 사이트 레이팅 지수 없음. 0.0으로 고정.");

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

        } finally {
            log.info("크롤링 종료");
            log.info("=============================");
        }
    }

    public void doNotEnterThisLink() {
        disAllowedLink.add("/addon/");
        disAllowedLink.add("/ch/");
        disAllowedLink.add("/files/");
        disAllowedLink.add("/tpl/");
        disAllowedLink.add("/widget/");
        disAllowedLink.add("/page/goods_event");

        for (String disAllowed : disAllowedLink) {
            log.info("접근금지 링크 : {}", disAllowed);
        }

    }

}

