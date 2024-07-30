package com.sparta.elevenbookshelf.controller;


import com.sparta.elevenbookshelf.dto.ContentResponseDto;
import com.sparta.elevenbookshelf.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<List<ContentResponseDto>> readContent(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContent(offset, pagesize));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<ContentResponseDto> readComments(@PathVariable Long cardId) {
        return ResponseEntity.status(HttpStatus.OK).
                body(contentService.readContent(cardId));
    }

}
