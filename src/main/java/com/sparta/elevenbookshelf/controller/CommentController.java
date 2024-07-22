package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.entity.Comment;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{boardId}")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long boardId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                            @RequestBody CommentRequestDto commentRequestDto){

        return ResponseEntity.status(HttpStatus.CREATED).
                body(commentService.createComment(boardId, userPrincipal, commentRequestDto));
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDto>> readComments(@PathVariable Long boardId){

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.readComments(boardId));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long boardId,
                                                            @PathVariable Long commentId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal,
                                                            @RequestBody CommentRequestDto commentRequestDto){

        return ResponseEntity.status(HttpStatus.OK).
                body(commentService.updateComment(boardId, commentId, userPrincipal.getUser(), commentRequestDto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long boardId,
                                                            @PathVariable Long commentId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal){

        commentService.deleteComment(boardId, commentId, userPrincipal.getUser());
        return ResponseEntity.ok().build();
    }
}
