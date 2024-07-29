package com.sparta.elevenbookshelf.crawling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrawlingTestRepository extends JpaRepository<CrawlingTest, Long> {

    Optional<CrawlingTest> findByUrl(String artUrl);

}
