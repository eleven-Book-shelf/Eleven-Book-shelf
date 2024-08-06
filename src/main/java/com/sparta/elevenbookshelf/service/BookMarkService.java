package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.entity.BookMark;
import com.sparta.elevenbookshelf.entity.Content;
import com.sparta.elevenbookshelf.entity.Hashtag;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.entity.mappingEntity.ContentHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.PostHashtag;
import com.sparta.elevenbookshelf.entity.mappingEntity.UserHashtag;
import com.sparta.elevenbookshelf.entity.post.Post;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.bookmarkRepository.BookMarkRepository;
import com.sparta.elevenbookshelf.repository.contentRepository.ContentRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.ContentHashtagRepository;
import com.sparta.elevenbookshelf.repository.hashtagRepository.UserHashtagRepository;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private static final Double BOOKMARK_WEIGHT = 5.0;
    private static final Double BOOKMARKED_WEIGHT = 5.0;

    private final BookMarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final HashtagService hashtagService;
    private final ContentHashtagRepository contentHashtagRepository;
    private final UserHashtagRepository userHashtagRepository;

    /**
     * 북마크 추가 기능
     * - 해당 유저와 콘텐츠를 찾고, 북마크를 추가합니다.
     * - 해시태그 가중치를 업데이트합니다.
     * - 관련 엔티티들을 저장합니다.
     * @param userId 유저 ID
     * @param contentId 콘텐츠 ID
     * @return BookMarkResponseDto 북마크 추가 후의 콘텐츠 정보
     */

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

        // hashtag 가중치 설정 부분
        Set<ContentHashtag> contentHashtags = content.getContentHashtags();

        for (ContentHashtag tag : contentHashtags) {

            Hashtag hashtag = hashtagService.createOrUpdateHashtag(tag.getHashtag().getTag());

            UserHashtag userHashtag = hashtagService.createOrUpdateUserHashtag(user, hashtag, BOOKMARK_WEIGHT);
            user.addHashtag(userHashtag);

            ContentHashtag contentHashtag = hashtagService.createOrUpdateContentHashtag(content, hashtag, BOOKMARKED_WEIGHT);
            content.addHashtag(contentHashtag);
        }

        userRepository.save(user);
        userHashtagRepository.saveAll(user.getUserHashtags());
        contentRepository.save(content);
        contentHashtagRepository.saveAll(content.getContentHashtags());

        bookmark.toggleStatus();
        bookmarkRepository.save(bookmark);
        content.addBockMarkCount();
        return BookMarkResponseDto.fromPost(content);
    }

    /**
     * 북마크 제거 기능
     * - 해당 유저와 콘텐츠를 찾고, 북마크를 제거합니다.
     * @param userId 유저 ID
     * @param contentId 콘텐츠 ID
     */
    @Transactional
    @CacheEvict(value = "bookMarkCache", key = "#userId + '-' + #contentId")
    public void removeBookMark(Long userId, Long contentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CONTENT));

        bookmarkRepository.deleteByUserAndPost(userId, contentId);
    }

    /**
     * 유저의 북마크 목록을 조회하는 기능
     * - 해당 유저의 북마크 목록을 페이지 단위로 조회합니다.
     * @param userId 유저 ID
     * @param offset 페이지 오프셋
     * @param pageSize 페이지 크기
     * @return List<BookMarkResponseDto> 북마크 목록
     */
    @Transactional
    @Cacheable(value = "bookMarkCache", key = "#userId + '-' + #offset + '-' + #pageSize")
    public List<BookMarkResponseDto> getUserBookMarks(Long userId, Long offset, int pageSize) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return bookmarkRepository.findAllByUser(userId, offset, pageSize).stream()
                .map(bookmark -> BookMarkResponseDto.fromPost(bookmark.getContent()))
                .collect(Collectors.toList());
    }

    /**
     * 북마크 여부 확인 기능
     * - 해당 유저가 특정 콘텐츠를 북마크했는지 확인합니다.
     * @param userId 유저 ID
     * @param contentId 콘텐츠 ID
     * @return boolean 북마크 여부
     */
    @Transactional
    public boolean isBookMarked(Long userId, Long contentId) {
        return bookmarkRepository.existsByUserIdAndContentId(userId, contentId);
    }
}
