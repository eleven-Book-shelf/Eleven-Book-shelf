package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookmarkService;

    @PostMapping("/{postId}")
    public ResponseEntity<BookMarkResponseDto> addBookMark(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        bookmarkService.addBookMark(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        bookmarkService.removeBookMark(userPrincipal.getUser().getId(), postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/status")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @PathVariable Long postId) {
        boolean isBookmarked = bookmarkService.isBookMarked(userId, postId);
        return ResponseEntity.ok(isBookmarked);
    }

/*    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookMarkResponseDto>> getUserBookmarks(@PathVariable Long userId) {
        List<BookMarkResponseDto> bookmarks = bookmarkService.getUserBookMarks(userId);
        return ResponseEntity.ok(bookmarks);
    }*/
}