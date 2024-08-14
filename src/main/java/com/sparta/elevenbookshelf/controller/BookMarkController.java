package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookmarkService;

    @PostMapping("/{postId}")
    public ResponseEntity<Void> addBookMark(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        bookmarkService.addBookMark(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        bookmarkService.removeBookMark(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookMarkResponseDto>> getUserBookmarks(
            @PathVariable Long userId,
            @RequestParam(value = "offset", defaultValue = "0") Long offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        List<BookMarkResponseDto> bookmarks = bookmarkService.getUserBookMarks(userId, offset, pagesize);
        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/{postId}/status")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @PathVariable Long postId) {
        boolean isBookmarked = bookmarkService.isBookMarked(userId, postId);
        return ResponseEntity.ok(isBookmarked);
    }
}