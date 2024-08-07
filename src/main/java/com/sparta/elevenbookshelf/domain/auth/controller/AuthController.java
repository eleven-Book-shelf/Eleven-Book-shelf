package com.sparta.elevenbookshelf.domain.auth.controller;

import com.sparta.elevenbookshelf.domain.auth.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.domain.auth.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.domain.auth.service.AuthService;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto req) {
        LoginResponseDto loginResponseDto = authService.login(req);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal UserPrincipal userPrincipal){
        authService.logout(userPrincipal.getUser().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestParam String token){
        LoginResponseDto loginResponseDto = authService.refresh(token);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

}
