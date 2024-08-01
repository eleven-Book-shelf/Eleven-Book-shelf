package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{postId}")
public class CommentController {

    private final CommentService commentService;

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping("/comments")
    public ResponseEntity<Void> createCommentPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {
        commentService.createComment(postId, userPrincipal, commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDto>> readCommentsPost(
            @PathVariable Long postId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.readCommentsPost(postId, offset, pagesize));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {
            commentService.updateComment(postId, commentId, userPrincipal.getUser(), commentRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        commentService.deleteComment(postId, commentId, userPrincipal.getUser());
        return ResponseEntity.ok().build();
    }

}
