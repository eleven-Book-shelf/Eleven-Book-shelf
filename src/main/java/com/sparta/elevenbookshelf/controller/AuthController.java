package com.sparta.elevenbookshelf.controller;

import com.sparta.elevenbookshelf.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto req) {

        LoginResponseDto res = authService.login(req);

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION,res.getAccessToken()).body(res);
    }

    @PatchMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal userPrincipal){
        authService.logout(userPrincipal.getUser());
        return ResponseEntity.status(HttpStatus.OK).build();
    }



}
