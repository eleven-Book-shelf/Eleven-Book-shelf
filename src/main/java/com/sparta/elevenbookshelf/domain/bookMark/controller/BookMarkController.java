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

    @PostMapping("/{postId}")
    public ResponseEntity<BookMarkResponseDto> addBookMark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        BookMarkResponseDto res = bookmarkService.addBookMark(userPrincipal.getUser().getId(), postId);
        hashtagService.userContentHashtagInteraction(userPrincipal.getUser().getId(), postId, hashtagService.BOOKMARK_WEIGHT, hashtagService.BOOKMARKED_WEIGHT);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        bookmarkService.removeBookMark(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookMarkResponseDto>> getUserBookmarks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "offset", defaultValue = "0") Long offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        List<BookMarkResponseDto> bookmarks = bookmarkService.getUserBookMarks(userPrincipal.getUser().getId(), offset, pagesize);
        return ResponseEntity.status(HttpStatus.OK).body(bookmarks);
    }

    @GetMapping("/{postId}/status")
    public ResponseEntity<Boolean> isBookmarked(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        boolean isBookmarked = bookmarkService.isBookMarked(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.status(HttpStatus.OK).body(isBookmarked);
    }
}