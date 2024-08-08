package com.sparta.elevenbookshelf.domain.admin.controller;

import com.sparta.elevenbookshelf.domain.admin.dto.AdminUserStatusUpdateDto;
import com.sparta.elevenbookshelf.domain.admin.service.AdminUserService;
import com.sparta.elevenbookshelf.domain.auth.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.domain.auth.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.domain.auth.service.AuthService;
import com.sparta.elevenbookshelf.domain.user.dto.UserRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUserService adminUserService;

    // 유저 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> showUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    //유저 상태 변경
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestBody AdminUserStatusUpdateDto requestDto) {
        adminUserService.updateUserStatus(userId, requestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }







}
