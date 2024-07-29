package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.entity.*;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.BookMarkRepository;
import com.sparta.elevenbookshelf.repository.postRepository.PostRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final BookMarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public BookMarkResponseDto addBookMark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        BookMark bookmark = bookmarkRepository.findByUserAndPost(user, post)
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
    public void removeBookMark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        bookmarkRepository.deleteByUserAndPost(user, post);
    }

//    @Transactional
//    public boolean isBookMarked(Long userId, Long postId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
//
//        return bookmarkRepository.findByUserAndPost(user, post).isPresent();
//    }

    @Transactional
    public List<BookMarkResponseDto> getUserBookMarks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return bookmarkRepository.findAllByUser(user).stream()
                .map(bookmark -> BookMarkResponseDto.fromPost(bookmark.getPost()))
                .collect(Collectors.toList());
    }
}
