package com.sparta.elevenbookshelf.crawling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlingTestRepository extends JpaRepository<CrawlingTest, Long> {

}
