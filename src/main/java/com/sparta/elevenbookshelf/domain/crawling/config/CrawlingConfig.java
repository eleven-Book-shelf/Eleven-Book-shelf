package com.sparta.elevenbookshelf.domain.crawling.config;

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
        //사용할 WebDriver 의 기본적인 설정을 구성하는 부분.

        // 크롬 드라이버가 어디에 존재하는지 찾아서 사용하는 부분. 현재는 웹드라이버매니저를 사용하여 자동으로 찾게함.(로컬에서만 가능)
        WebDriverManager.chromedriver().setup();



        // 크롬 브라우저를 어떤 상태로 사용할 것인지 설정하는 부분.
        ChromeOptions options = new ChromeOptions();

        // 크롤링을 할때 크롬이 실시간으로 어디에 들어가는지 보고싶지 않을때 사용하는 백그라운드 실행 명령
        options.addArguments("--headless");

        // 크롬의 샌드박스 모드를 비활성화 하여 도커 컨테이너 내부에서 충돌 방지.
        options.addArguments("--no-sandbox");

        // 크롬이 리눅스에서 공유 메모리에 맵 파일을 저장하지 않도록 설정. 도커와 같은 컨테이너 환경에서는 용량이 제한적일수 있기에 설정함.
        options.addArguments("--disable-dev-shm-usage");

        // 설정이 완료된 웹드라이버 객체 반환.
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
