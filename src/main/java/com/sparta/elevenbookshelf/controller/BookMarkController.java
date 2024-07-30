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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookMarkResponseDto>> getUserBookmarks(@PathVariable Long userId,
                                                                      @RequestParam(value = "offset", defaultValue = "0") Long offset,
                                                                      @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        List<BookMarkResponseDto> bookmarks = bookmarkService.getUserBookMarks(userId, offset, pagesize);
        return ResponseEntity.ok(bookmarks);
    }
}