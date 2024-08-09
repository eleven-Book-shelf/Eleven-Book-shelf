package com.sparta.elevenbookshelf.common.crawling;

import com.sparta.elevenbookshelf.domain.content.dto.ContentRequestDto;
import com.sparta.elevenbookshelf.domain.content.entity.Content;
import com.sparta.elevenbookshelf.domain.content.repository.ContentRepository;
import com.sparta.elevenbookshelf.domain.content.service.ContentService;
import com.sparta.elevenbookshelf.domain.hashtag.entity.Hashtag;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingUtil")
public class CrawlingUtil {

    private final WebDriver webDriver;
    private final ContentRepository contentRepository;
    private final HashtagService hashtagService;
    private final ContentService contentService;

    @Value("${csv.file}")
    private String csvOutputDirectory;

    @Value("${csv.file_locate}")
    private String csvFileLocate;

    // robots.txt 규약 준수를 위한 URL 검사 메서드.
    public boolean checkTheLink(String url, Set<String> disAllowedLink) {
        for (String disallowedPath : disAllowedLink) {
            if (url.contains(disallowedPath)) {
                return true;
            }
        }
        return false;
    }

    // 중복 링크 제거.
    public Set<String> notDuplicatedLinks(By id, By cssSelector) {
        Set<String> uniqueLinks = new HashSet<>();
        try {
            WebElement parentElement = webDriver.findElement(id);
            List<WebElement> linkElements = parentElement.findElements(cssSelector);
            for (WebElement element : linkElements) {
                String uniqueLink = element.getAttribute("href");
                uniqueLinks.add(uniqueLink);
            }
            log.info("유일한 링크 개수 : {} ", uniqueLinks.size());
        } catch (NoSuchElementException e) {
            log.error("요소를 찾을 수 없습니다: {}", e.getMessage());
        }
        return uniqueLinks;
    }


    // 페이지 로딩을 기다리는 메서드.
    public void waitForPage() {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        wait.until(webDriver
                -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState")
                .equals("complete"));
    }

    // 복수 : css 로딩을 기다리는 메서드.
    public List<WebElement> waitForElements(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    // 단수 : css 로딩을 기다리는 메서드.
    public WebElement waitForElement(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // header 에 meta 데이터에서 추출하는 메서드. 로그인이 필요한 페이지여도 추출이 가능함.
    public String metaData(String xPath, String attributeName) {
        WebElement element = waitForElement(By.xpath(xPath), 10);
        return element.getAttribute(attributeName);
    }

    // body 에서 데이터를 추출하는 메서드. 텍스트만 가져옴.
    public String bodyData(String xPath) {
        WebElement element = waitForElement(By.xpath(xPath), 10);
        return element.getText();
    }

    // 여러 클래스에서 텍스트 가져오기.
    public List<String> getHashtags(String locate) {
        List<WebElement> elements = waitForElements(By.xpath(locate), 10);
        List<String> hashtagList = new ArrayList<>();
        for (WebElement element : elements) {
            String text = element.getText();
            text = text.replaceAll("[\\s\\u00A0]+", "").trim();
            if (!text.contains("#")) {
                continue;
            }
            hashtagList.add(text);
        }

        if (hashtagList.isEmpty()) {
            hashtagList.add("없음");
        }

        return hashtagList;
    }

    // 썸네일 링크 가져오기
    public String getThumbnail(String selector, boolean isCssSelector) {
        WebElement element;
        if (isCssSelector) {
            element = waitForElement(By.cssSelector(selector), 10);
        } else {
            element = waitForElement(By.xpath(selector), 10);
        }

        return element.getAttribute("src");

    }

    // 동적 페이지 스크롤 최 하단 이동 메서드.
    // End 를 눌렀을 경우 페이지 최 하단으로 이동하는 페이지에 사용.
    public void scrollController() {
        log.info("Selenium 동적 스크롤 실행.");
        Actions actions = new Actions(webDriver);

        for (int i = 0; i <= 20; i++) {
            log.info("반복 횟수 : {}", i);
            actions.sendKeys(Keys.END).perform();
            sleep(1000); // 1초 대기하여 로딩 시간을 줌
            waitForPage();
            actions.sendKeys(Keys.HOME).perform();
            sleep(500);
            waitForPage();
        }

        waitForPage();

    }

    // 동적페이지에서 스크롤을 끝까지 내려주는 메서드.
    // End 를 눌렀을때 페이지의 최 하단으로 이동하지 않는 페이지에 사용
    public void scrollToEndOfPage() {
        // 자바 스크립트 코드를 실행 할 수 있도록 JavascriptExecutor 객체 생성.
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        log.info("JS 동적 스크롤 실행.");

        // 현재 페이지의 총 높이를 저장.
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
        log.info("현재 높이 : {}", lastHeight);

        // 스크롤이 바닥에 닿을때까지 반복
        while (true) {
            // 페이지를 끝까지 스크롤함.
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            sleep(500);

            // 스크롤을 최하단까지 내린후 페이지의 총 높이를 다시 저장.
            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            log.info("스크롤 후 페이지 높이 : {}", newHeight);

            // 이번 반복에서 저장한 페이지의 총 높이와 이전 반복에서 저장한 페이지의 총 높이가 같다면 반복을 종료.
            if (newHeight == lastHeight) {
                log.info("페이지 끝 도착.");
                break;
            }

            // 마지막 반복의 높이값을 계속 업데이트하여 조건문에서 검사가 가능하도록 함.
            lastHeight = newHeight;
            log.info("페이지 높이 다름. 반복 실시.");
        }
    }

    // 쓰레드 슬립으로 표적 서버 부하 줄이기.
    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("쓰레드 슬립 도중 에러 발생. : {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // 데이터 파일을 DB에 저장하는 메서드
    @Transactional
    public void saveData(ContentRequestDto req, String artUrl) {
        Optional<Content> dataSave = contentRepository.findByUrl(artUrl);
        if (dataSave.isPresent()) {

            Content content = dataSave.get();
            contentService.updateContent(content, req);

        } else {

            Content content = contentService.createContent(req);
            List<Hashtag> hashtags = hashtagService.updateAndSaveHashtags(req.getContentHashTag() + req.getGenre());
            hashtagService.updateAndSaveHashtags(content, hashtags, hashtagService.INIT_WEIGHT);
        }

    }

    // csv 파일로 변환하여 저장.
    public void exportToCsv() {
        log.info("파일 저장 메서드 실행");
        List<Content> allData = contentRepository.findAll();

        String fileName = "crawling_data.csv";
        Path filePath = Paths.get(csvOutputDirectory, fileName);
        log.info("파일 저장 위치 : {}", csvOutputDirectory);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     // setHeader 는 저장될 데이터의 필드 네임을 작성.
                     .setHeader(
                             "Id",
                             "Title",
                             "Description",
                             "Author",
                             "Platform",
                             "Url",
                             "Genre",
                             "ContentHashTag",
                             "View",
                             "Rating",
                             "BookMarkCount",
                             "LikeCount",
                             "Type",
                             "IsEnd",
                             "ImgUrl"
                             )
                     .build())) {

            for (Content data : allData) {
                // 실제 데이터가 들어가는 부분. 데이터의 위치를 변경한다면 헤더의 위치도 변경해주어야 함.
                printer.printRecord(
                        data.getId(),
                        data.getTitle(),
                        data.getDescription(),
                        data.getAuthor(),
                        data.getPlatform(),
                        data.getUrl(),
                        data.getGenre(),
                        data.getContentHashTag(),
                        data.getView(),
                        data.getRating(),
                        data.getBookMarkCount(),
                        data.getLikeCount(),
                        data.getType().toString(),
                        data.getIsEnd().toString(),
                        data.getImgUrl()
                );
            }

            log.info("CSV 파일이 생성되었습니다: {}", filePath);

        } catch (IOException e) {
            log.error("CSV 파일 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // 로컬에 저장된 파일로 DB 업데이트하는 메서드
    public void updateDatabase() {
        Path csvFile = Paths.get(csvFileLocate);
        try (FileReader reader = new FileReader(csvFile.toFile());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()))
        {
            for (CSVRecord csvRecord : csvParser) {
                String artUrl = csvRecord.get("Url");

                Optional<Content> mainData = contentRepository.findByUrl(artUrl);

                if (mainData.isPresent()) {
                    ContentRequestDto requestDto = createRequestDto(csvRecord);
                    updateData(mainData.get(), requestDto);
                    contentRepository.save(mainData.get());

                } else {
                    Content newData = createNewData(csvRecord);
                    contentRepository.save(newData);
                }
            }

        } catch (IOException e) {
            log.error("CSV 파일 읽는중 오류 발생 : {}", e.getMessage());
        } catch (NumberFormatException e) {
            log.error("ID 파싱 중 오류 발생 : {}", e.getMessage());
        } catch (NullPointerException e) {
            log.error("DB 업데이트 중 NPE 발생. : {}", e.getMessage());
        }

    }

    // Content 엔티티의 작성한 객체 update 메서드 사용.
    private void updateData(Content existingData, ContentRequestDto requestDto) {
        existingData.updateContent(requestDto);
    }

    // csv 파일 변환을 위한 메서드. 로컬에 저장된 데이터를 필요한 데이터에 필요한 파싱을 적용해 DB에 저장한다.
    private Content createNewData(CSVRecord csvRecord) {
        return Content.builder()
                .title(csvRecord.get("Title"))
                .description(csvRecord.get("Description"))
                .author(csvRecord.get("Author"))
                .platform(csvRecord.get("Platform"))
                .url(csvRecord.get("Url"))
                .genre(csvRecord.get("Genre"))
                .contentHashTag(csvRecord.get("ContentHashTag"))
                .view(parseDoubleOrDefault(csvRecord.get("View")))
                .rating(parseDoubleOrDefault(csvRecord.get("Rating")))
                .bookMarkCount(parseLongOrDefault(csvRecord.get("BookMarkCount")))
                .likeCount(parseLongOrDefault(csvRecord.get("LikeCount")))
                .type(Content.ContentType.valueOf(csvRecord.get("Type").toUpperCase()))
                .isEnd(Content.ContentEnd.valueOf(csvRecord.get("IsEnd").toUpperCase()))
                .imgUrl(csvRecord.get("ImgUrl"))
                .build();

    }

    private ContentRequestDto createRequestDto(CSVRecord csvRecord) {
        ContentRequestDto requestDto = new ContentRequestDto();
        requestDto.setTitle(csvRecord.get("Title"));
        requestDto.setImgUrl(csvRecord.get("ImgUrl"));
        requestDto.setDescription(csvRecord.get("Description"));
        requestDto.setAuthor(csvRecord.get("Author"));
        requestDto.setPlatform(csvRecord.get("Platform"));
        requestDto.setUrl(csvRecord.get("Url"));
        requestDto.setGenre(csvRecord.get("Genre"));
        requestDto.setContentHashTag(csvRecord.get("ContentHashTag"));
        requestDto.setView(parseDoubleOrDefault(csvRecord.get("View")));
        requestDto.setRating(parseDoubleOrDefault(csvRecord.get("Rating")));
        requestDto.setBookMarkCount(parseLongOrDefault(csvRecord.get("BookMarkCount")));
        requestDto.setLikeCount(parseLongOrDefault(csvRecord.get("LikeCount")));
        requestDto.setType(Content.ContentType.valueOf(csvRecord.get("Type").toUpperCase()));
        requestDto.setIsEnd(Content.ContentEnd.valueOf(csvRecord.get("IsEnd").toUpperCase()));
        return requestDto;
    }

    // Long 타입으로 파싱
    private Long parseLongOrDefault(String data) {

        if (data == null || data.trim().isEmpty()) {
            log.warn("Long 파싱중 입력받은 데이터가 비어있습니다. : {}", data);
            return 0L;
        }

        try {
            return Long.parseLong(data);
        } catch (NumberFormatException e) {
            log.error("Long 형태로 변환중 에러 발생 : {}", e.getMessage());
            return 0L;
        }

    }

    // Double 타입으로 파싱
    private Double parseDoubleOrDefault(String data) {

        if (data == null || data.trim().isEmpty()) {
            log.error("Double 파싱중 입력받은 데이터가 비어있습니다. : {}", data);
            return 0.0;
        }

        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            log.error("Double 형태로 변환중 에러 발생 : {}", e.getMessage());
            return 0.0;
        }

    }

}


