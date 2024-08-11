package com.sparta.elevenbookshelf.domain.comment.controller;

import com.sparta.elevenbookshelf.domain.comment.dto.CommentMapResponseDto;
import com.sparta.elevenbookshelf.domain.comment.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.domain.comment.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.domain.comment.service.CommentService;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
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
    private final HashtagService hashtagService;

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping
    public ResponseEntity<Void> createCommentPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {

        commentService.createComment(postId, userPrincipal, commentRequestDto);

        // 댓글 작성 시 해시태그 갱신
        hashtagService.userPostHashtagInteraction(userPrincipal.getUser().getId(), postId, hashtagService.COMMENT_WEIGHT);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<CommentMapResponseDto> readCommentsPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam( defaultValue = "10") int pagesize) {

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.readCommentsPost(postId, page, pagesize));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CommentRequestDto commentRequestDto) {
            commentService.updateComment(postId, commentId, userPrincipal.getUser().getId(), commentRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        commentService.deleteComment(postId, commentId, userPrincipal.getUser().getId());
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
