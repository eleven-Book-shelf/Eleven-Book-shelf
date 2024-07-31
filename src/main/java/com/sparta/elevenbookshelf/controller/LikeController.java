package com.sparta.elevenbookshelf.controller;

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
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{id}/likes")
    public ResponseEntity<LikeResponseDto> createLikeComment(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.status(HttpStatus.CREATED).
                body(likeService.createLikeComment(id, userPrincipal.getUser().getId()));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<Void> DeleteLikeComment(
                    @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikeComment(id, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<Boolean> getLikeComment(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(likeService.getLikeComment(id, userPrincipal.getUser().getId()));
    }

    //:::::::::::::::::// Content //::::::::::::::::://

    @PostMapping("/{id}/likesContent")
    public ResponseEntity<Void> createLikeContent(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.createLikeContent(id, userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/likesContent")
    public ResponseEntity<Void> deleteLikeContent(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikeContent(id, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/likesContent")
    public ResponseEntity<Boolean> getLikeContent(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(likeService.getLikeContent(id, userPrincipal.getUser().getId()));
    }

    //:::::::::::::::::// post //::::::::::::::::://

    @PostMapping("/{id}/likesPost")
    public ResponseEntity<Void> createLikePost(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.createLikePost(id, userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/likesPost")
    public ResponseEntity<Void> deleteLikePost(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        likeService.deleteLikePost(id, userPrincipal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/likesPost")
    public ResponseEntity<Boolean> getLikePost(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal){

        return ResponseEntity.ok().body(likeService.getLikePost(id, userPrincipal.getUser().getId()));
    }
}
