package com.sparta.elevenbookshelf.crawling;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class CrawlingTest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crawling_test_id")
    private Long id;
    
    private String title;

    private String author;

    private String site;

    private String comicsOrBook;

    private String completeOrNot;

    private Double totalView;

    private Double rating;

    private String releaseDay;

}
