package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.BookMarkRequestDto;
import com.sparta.elevenbookshelf.dto.BookMarkResponseDto;
import com.sparta.elevenbookshelf.service.BookMarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookmarkService;

    @PostMapping("/{postId}")
    public ResponseEntity<BookMarkResponseDto> addBookMark(@RequestBody BookMarkRequestDto requestDto, @PathVariable Long postId) {
        BookMarkResponseDto response = bookmarkService.addBookMark(requestDto.getUserId(), postId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removeBookmark(@RequestBody BookMarkRequestDto requestDto, @PathVariable Long postId) {
        bookmarkService.removeBookMark(requestDto.getUserId(), postId);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/{postId}/status")
//    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @PathVariable Long postId) {
//        boolean isBookmarked = bookmarkService.isBookMarked(userId, postId);
//        return ResponseEntity.ok(isBookmarked);
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookMarkResponseDto>> getUserBookmarks(@PathVariable Long userId) {
        List<BookMarkResponseDto> bookmarks = bookmarkService.getUserBookMarks(userId);
        return ResponseEntity.ok(bookmarks);
    }
}