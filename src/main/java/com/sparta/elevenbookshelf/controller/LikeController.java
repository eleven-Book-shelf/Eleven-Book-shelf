package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.CommentRequestDto;
import com.sparta.elevenbookshelf.dto.CommentResponseDto;
import com.sparta.elevenbookshelf.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/comments/{id}/likes")
    public ResponseEntity<LikeResponseDto> createLikeComment(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.status(HttpStatus.CREATED).
                body(likeService.createLikeComment(id, userPrincipal.getUser()));
    }

    @DeleteMapping("/comments/{id}/likes")
    public ResponseEntity<Void> DeleteLikeComment(@PathVariable Long id,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikeComment(id, userPrincipal.getUser());
        return ResponseEntity.ok().build();
    }
}
