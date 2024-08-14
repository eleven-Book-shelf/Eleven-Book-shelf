package com.sparta.elevenbookshelf.domain.bookMark.controller;

import com.sparta.elevenbookshelf.domain.bookMark.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.domain.bookMark.service.BookMarkService;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookmarkService;
    private final HashtagService hashtagService;

    // 컨탠츠 북마크
    @PostMapping("/{contentsId}")
    public ResponseEntity<BookMarkResponseDto> addBookMark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long contentsId) {

        BookMarkResponseDto res = bookmarkService.addBookMark(userPrincipal.getUser().getId(), contentsId);
        hashtagService.userContentHashtagInteraction(userPrincipal.getUser().getId(), contentsId, hashtagService.BOOKMARK_WEIGHT, hashtagService.BOOKMARKED_WEIGHT);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 컨탠츠 북마크 취소
    @DeleteMapping("/{contentsId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long contentsId) {

        bookmarkService.removeBookMark(userPrincipal.getUser().getId(), contentsId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 컨탠츠 북마크 상태
    @GetMapping("/{contentsId}/status")
    public ResponseEntity<Boolean> isBookmarked(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long contentsId) {

        boolean isBookmarked = bookmarkService.isBookMarked(userPrincipal.getUser().getId(), contentsId);
        return ResponseEntity.status(HttpStatus.OK).body(isBookmarked);
    }

    // 유저 북마크 리스트
    @GetMapping("/user")
    public ResponseEntity<List<BookMarkResponseDto>> getUserBookmarks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") Long offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        List<BookMarkResponseDto> bookmarks = bookmarkService.getUserBookMarks(userPrincipal.getUser().getId(), offset, pagesize);
        return ResponseEntity.status(HttpStatus.OK).body(bookmarks);
    }

}