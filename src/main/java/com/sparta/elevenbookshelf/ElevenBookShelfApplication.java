package com.sparta.elevenbookshelf;

import com.sparta.elevenbookshelf.crawling.CrawlingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ElevenBookShelfApplication implements CommandLineRunner {

    private final CrawlingService crawlingService;

    public ElevenBookShelfApplication(CrawlingService crawlingService) {
        this.crawlingService = crawlingService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ElevenBookShelfApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        crawlingService.performGoogleSearch();
    }

}
