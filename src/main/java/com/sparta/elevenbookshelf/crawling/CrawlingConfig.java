package com.sparta.elevenbookshelf.crawling;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrawlingConfig {

    private WebDriver webDriver;

    @Bean
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        webDriver = new ChromeDriver(options);
        return webDriver;
    }

    @PreDestroy
    public void quitDriver() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

}
