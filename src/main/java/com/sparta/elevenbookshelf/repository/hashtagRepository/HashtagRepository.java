package com.sparta.elevenbookshelf.repository.hashtagRepository;

import com.sparta.elevenbookshelf.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    @Query("SELECT h FROM Hashtag h ORDER BY h.count DESC")
    List<Hashtag> findTop10ByCount();

    Optional<Hashtag> findByTag(String tag);
}
