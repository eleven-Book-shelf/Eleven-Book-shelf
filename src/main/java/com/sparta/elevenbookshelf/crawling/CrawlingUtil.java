package com.sparta.elevenbookshelf.crawling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CrawlingUtil")
public class CrawlingUtil {

    private final WebDriver webDriver;

    // robots.txt 규약 준수를 위한 URL 검사 메서드.
    public boolean checkTheLink(String url, Set<String> disAllowedLink) {
        for (String disallowedPath : disAllowedLink) {
            if (url.contains(disallowedPath)) {
                return true;
            }
        }
        return false;
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

    // body 에서 데이터를 추출하는 메서드.
    public String bodyData(String xPath) {
        WebElement element = waitForElement(By.xpath(xPath), 10);
        return element.getText();
    }

    // 동적 페이지 스크롤 최 하단 이동 메서드.
    // End 를 눌렀을 경우 페이지 최 하단으로 이동하는 페이지에 사용.
    public void scrollController() {
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

        // 현재 페이지의 총 높이를 저장.
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");

        // 스크롤이 바닥에 닿을때까지 반복
        while (true) {
            // 페이지를 끝까지 스크롤함.
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            sleep(1000);

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

    // 쓰레드 슬립으로 표적 서버 부하 줄이기.
    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            log.error("쓰레드 슬립 도중 에러 발생. : {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}


