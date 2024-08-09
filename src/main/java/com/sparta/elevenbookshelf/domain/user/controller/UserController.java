package com.sparta.elevenbookshelf.domain.user.controller;

import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import com.sparta.elevenbookshelf.domain.user.dto.UserRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
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
    private final PostService postService;

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

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getUserPosts(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "pagesize", defaultValue = "20") int pagesize) {

        List<PostResponseDto> res = postService.readPostsByUser(user.getUser().getId(), offset, pagesize);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editProfile(@AuthenticationPrincipal UserPrincipal user ,@RequestParam String username) {
        log.info("editProfile 실행");
        UserResponseDto res = userService.editProfile(user.getUser().getId() , username );
        return ResponseEntity.noContent().build();
    }

}
