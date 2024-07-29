package com.sparta.elevenbookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ElevenBookShelfApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElevenBookShelfApplication.class, args);
    }

}