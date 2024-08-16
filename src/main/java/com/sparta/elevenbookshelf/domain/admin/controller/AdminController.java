package com.sparta.elevenbookshelf.domain.admin.controller;

import com.sparta.elevenbookshelf.domain.admin.dto.AdminUserStatusUpdateDto;
import com.sparta.elevenbookshelf.domain.admin.service.AdminService;
import com.sparta.elevenbookshelf.domain.content.dto.ContentAdminResponseDto;
import com.sparta.elevenbookshelf.domain.hashtag.dto.HashtagResponseDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostMapResponseDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostRequestDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseDto;
import com.sparta.elevenbookshelf.domain.post.dto.PostResponseListDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 유저 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDto> showUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUser(userId));
    }

    // 유저 전체 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/page")
    public ResponseEntity<List<UserResponseDto>> getUserPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean asc) {
        return ResponseEntity.ok(adminService.getAdminUserPage(page, size, sortBy, asc));
    }

    //유저 상태 변경
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestBody AdminUserStatusUpdateDto requestDto) {
        adminService.updateUserStatus(userId, requestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //포스트 전체조회 관리
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post/page")
    public ResponseEntity<List<PostResponseDto>> getPostPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean asc) {
        return ResponseEntity.ok(adminService.getPostPage(page, size, sortBy, asc));
    }

    //포스트 관리
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> deletePostAdmin(@PathVariable Long postId) {
        adminService.deletePostAdmin(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    //해쉬태그 전체조회 관리
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hashtag/page")
    public ResponseEntity<List<HashtagResponseDto>> getHashtagPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean asc) {
        return ResponseEntity.ok(adminService.getHashtagPage(page, size, sortBy, asc));
    }

    //해쉬태그 관리
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/hashtag/{hashtagId}")
    public ResponseEntity<?> updateHashtagPage(@PathVariable Long hashtagId) {
        adminService.updateHashtagPage(hashtagId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //컨탠츠 전체조회 관리
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/contents/page")
    public ResponseEntity<List<ContentAdminResponseDto>> getContentPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean asc) {
        return ResponseEntity.ok(adminService.getContentPage(page, size, sortBy, asc));
    }

    //컨탠츠 관리
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/contents/{contentId}")
    public ResponseEntity<?> updateContentPage(@PathVariable Long contentId) {
        adminService.updateContentPage(contentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //공지 사항 포스트
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/post/notice")
    public ResponseEntity<PostResponseDto> createNoticePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PostRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                adminService.createNoticePost(userPrincipal.getUser().getId(), requestDto));
    }

    //공지 사항 포스트 전체조회 관리
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/post/notice/page")
    public ResponseEntity<PostMapResponseDto> getNoticePostPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam boolean asc) {
        return ResponseEntity.ok(adminService.getNoticePostPage(page, size, asc));
    }




}
