package com.sparta.elevenbookshelf.domain.comment.controller;

import com.sparta.elevenbookshelf.domain.comment.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.domain.comment.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.domain.comment.service.CommentService;
import com.sparta.elevenbookshelf.domain.like.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.domain.like.service.LikeService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping
    public ResponseEntity<Void> createCommentPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {
        commentService.createComment(postId, userPrincipal, commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> readCommentsPost(
            @PathVariable Long postId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.readCommentsPost(postId, offset, pagesize));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {
            commentService.updateComment(postId, commentId, userPrincipal.getUser(), commentRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        commentService.deleteComment(postId, commentId, userPrincipal.getUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<LikeResponseDto> createLikeComment(
            @PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.status(HttpStatus.CREATED).
                body(likeService.createLikeComment(commentId, userPrincipal.getUser().getId()));
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> DeleteLikeComment(
            @PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikeComment(commentId, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}/like")
    public ResponseEntity<Boolean> getLikeComment(
            @PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(likeService.getLikeComment(commentId, userPrincipal.getUser().getId()));
    }

}
