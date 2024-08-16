package com.sparta.elevenbookshelf.domain.auth.controller;

import com.sparta.elevenbookshelf.domain.auth.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.domain.auth.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.domain.auth.service.AuthService;
import com.sparta.elevenbookshelf.domain.user.dto.UserRequestDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    //로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserPrincipal userPrincipal){
        authService.logout(userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 엑세스토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestParam String token){
        LoginResponseDto loginResponseDto = authService.refresh(token);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    // 회원가입 ( 주로  어드민용 )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDto req) {
        authService.signup(req);
        return ResponseEntity.noContent().build();
    }

    //회원 탈퇴
    @DeleteMapping("/signout")
    public ResponseEntity<?> signOut(@AuthenticationPrincipal UserPrincipal user) {
        authService.signOut(user.getUser().getId());
        return ResponseEntity.noContent().build();
    }

}
