package com.sparta.elevenbookshelf.repository.bookMarkRepository;

import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> ,BookMarkRepositoryCustom {
    Optional<BookMark> findByUserIdAndContentId(Long userId, Long ContentId);
    List<BookMark> findAllByUser(User user);
    void deleteByUserIdAndContentId(Long userId, Long contentId);
    boolean existsByUserIdAndContentId(Long userId, Long contentId);
}
