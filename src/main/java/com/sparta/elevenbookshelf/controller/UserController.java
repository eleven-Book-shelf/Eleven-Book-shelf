package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.UserHashtagRequestDto;
import com.sparta.elevenbookshelf.dto.UserHashtagResponseDto;
import com.sparta.elevenbookshelf.dto.UserRequestDto;
import com.sparta.elevenbookshelf.dto.UserResponseDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.HashtagService;
import com.sparta.elevenbookshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j(topic = "UserController")
public class UserController {

    private final UserService userService;
    private final HashtagService hashtagService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDto req) {
        userService.signup(req);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/signout")
    public ResponseEntity<?> signOut(@AuthenticationPrincipal UserPrincipal user) {
        userService.signOut(user.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserResponseDto> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        log.info("getProfile 실행");
        UserResponseDto res = userService.getProfile(user.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProfile(@AuthenticationPrincipal UserPrincipal user ,@RequestParam String username) {
        log.info("editProfile 실행");
        UserResponseDto res = userService.editProfile(user.getUser().getId() , username );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hashtag")
    public ResponseEntity<List<UserHashtagResponseDto>> getUserHashtags(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        log.info("getUserHashtag 실행");
        List<UserHashtagResponseDto> res = hashtagService.readUserHashtags(userPrincipal.getUser().getId(), limit);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/hashtag")
    public ResponseEntity<List<UserHashtagResponseDto>> updateUserHashtags(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                         @RequestBody UserHashtagRequestDto req) {
        log.info("updateUserHashtag 실행");
        List<UserHashtagResponseDto> res = hashtagService.updateUserHashtags(userPrincipal.getUser().getId(), req);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
