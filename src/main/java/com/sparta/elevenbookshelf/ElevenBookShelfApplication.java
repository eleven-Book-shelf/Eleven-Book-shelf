package com.sparta.elevenbookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class ElevenBookShelfApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElevenBookShelfApplication.class, args);
    }

}
