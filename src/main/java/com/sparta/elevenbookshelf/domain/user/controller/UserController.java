package com.sparta.elevenbookshelf.domain.user.controller;

import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagRequestDto;
import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagResponseDto;
import com.sparta.elevenbookshelf.domain.hashtag.service.HashtagService;
import com.sparta.elevenbookshelf.domain.post.dto.PostMapResponseDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import com.sparta.elevenbookshelf.domain.user.dto.UserPublicResponseDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j(topic = "UserController")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final HashtagService hashtagService;

    @GetMapping
    public ResponseEntity<UserResponseDto> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        log.info("getProfile 실행");
        UserResponseDto res = userService.getProfile(user.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserPublicResponseDto> getPublicUserData(@PathVariable Long userId) {
        UserPublicResponseDto res = userService.getPublicUserData(userId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/posts")
    public ResponseEntity<PostMapResponseDto> getUserPosts(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam boolean asc)  {

        PostMapResponseDto res = postService.readPostsByUser(user.getUser().getId(), page, pagesize,asc);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/posts/{userId}")
    public ResponseEntity<PostMapResponseDto> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pagesize,
            @RequestParam boolean asc)  {

        PostMapResponseDto res = postService.readPostsByUser(userId, page, pagesize,asc);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProfile(@AuthenticationPrincipal UserPrincipal user ,@RequestParam String username) {
        log.info("editProfile 실행");
        UserResponseDto res = userService.editProfile(user.getUser().getId() , username );
        return ResponseEntity.noContent().build();
    }

    //::::::::::::::::::::::::// Hashtag //:::::::::::::::::::::::://

    @GetMapping("/hashtags")
    public ResponseEntity<List<HashtagResponseDto>> getUserHashtags(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        log.info("getUserHashtag 실행");
        List<HashtagResponseDto> res = hashtagService.readUserHashtags(userPrincipal.getUser().getId(), limit);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/hashtags")
    public ResponseEntity<List<HashtagResponseDto>> updateUserHashtags(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody HashtagRequestDto req) {

        log.info("updateUserHashtag 실행");
        List<HashtagResponseDto> res = hashtagService.updateUserHashtags(userPrincipal.getUser().getId(), req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
