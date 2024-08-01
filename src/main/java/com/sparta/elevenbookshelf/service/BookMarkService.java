package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.bookmarkRepository.BookMarkRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final BookMarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    @Transactional
    @CacheEvict(value = "bookMarkCache", key = "#userId + '-' + #contentId")
    public BookMarkResponseDto addBookMark(Long userId, Long contentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));

        BookMark bookmark = bookmarkRepository.findByUserAndPost(userId, contentId)
                .orElse(BookMark.builder()
                        .user(user)
                        .content(content)
                        .status(true)
                        .build());

        bookmark.toggleStatus();
        bookmarkRepository.save(bookmark);
        content.addBockMarkCount();
        return BookMarkResponseDto.fromPost(content);
    }

    @Transactional
    @CacheEvict(value = "bookMarkCache", key = "#userId + '-' + #contentId")
    public void removeBookMark(Long userId, Long contentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));

        bookmarkRepository.deleteByUserAndPost(userId, contentId);
    }

    @Transactional
    @Cacheable(value = "bookMarkCache", key = "#userId + '-' + #offset + '-' + #pageSize")
    public List<BookMarkResponseDto> getUserBookMarks(Long userId, Long offset, int pageSize) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return bookmarkRepository.findAllByUser(userId, offset, pageSize).stream()
                .map(bookmark -> BookMarkResponseDto.fromPost(bookmark.getContent()))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean isBookMarked(Long userId, Long contentId) {
        return bookmarkRepository.existsByUserIdAndContentId(userId, contentId);
    }
}
