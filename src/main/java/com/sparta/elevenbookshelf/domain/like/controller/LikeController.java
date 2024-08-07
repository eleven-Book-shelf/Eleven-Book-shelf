package com.sparta.elevenbookshelf.domain.like.controller;

import com.sparta.elevenbookshelf.domain.like.dto.LikeResponseDto;
import com.sparta.elevenbookshelf.domain.like.service.LikeService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;

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
