package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.entity.*;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.bookMarkRepository.BookMarkRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.bookmarkRepository.BookMarkRepository;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
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
    public void addBookMark(Long userId, Long contentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));

        BookMark bookmark = bookmarkRepository.findByUserIdAndContentId(user.getId(), content.getId())
                .orElse(BookMark.builder()
                        .user(user)
                        .content(content)
                        .status(true)
                        .build());

        bookmark.toggleStatus();
        bookmarkRepository.save(bookmark);

//        return BookMarkResponseDto.fromPost(post);
    }

    @Transactional
    public void removeBookMark(Long userId, Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));

        bookmarkRepository.deleteByUserIdAndContentId(userId, contentId);
    }

    @Transactional
    public boolean isBookMarked(Long userId, Long contentId) {
        return bookmarkRepository.existsByUserIdAndContentId(userId, contentId);
    }

/*    @Transactional
    public List<BookMarkResponseDto> getUserBookMarks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return bookmarkRepository.findAllByUser(userId, offset, pageSize).stream()
                .map(bookmark -> BookMarkResponseDto.fromPost(bookmark.getPost()))
                .collect(Collectors.toList());
    }
    @Transactional
    @CacheEvict(value = "bookMarkCache", key = "#userId + '-' + #postId")
    public BookMarkResponseDto addBookMark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        BookMark bookmark = bookmarkRepository.findByUserAndPost(userId, postId)
                .orElse(BookMark.builder()
                        .user(user)
                        .post(post)
                        .status(true)
                        .build());

        bookmark.toggleStatus();
        bookmarkRepository.save(bookmark);

        return BookMarkResponseDto.fromPost(post);
    }

    @Transactional
    @CacheEvict(value = "bookMarkCache", key = "#userId + '-' + #postId")
    public void removeBookMark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        bookmarkRepository.deleteByUserAndPost(userId, postId);
    }

    @Transactional
    @Cacheable(value = "bookMarkCache", key = "#userId + '-' + #offset + '-' + #pageSize")
    public List<BookMarkResponseDto> getUserBookMarks(Long userId, Long offset, int pageSize) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return bookmarkRepository.findAllByUser(userId, offset, pageSize).stream()
                .map(bookmark -> BookMarkResponseDto.fromPost(bookmark.getPost()))
                .collect(Collectors.toList());
    }
    */
}
