package com.sparta.elevenbookshelf.domain.auth.service;


import com.sparta.elevenbookshelf.domain.auth.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.domain.auth.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword(),
                        null
                )
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUser(userPrincipal.getUsername());

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        user.addRefreshToken(refreshToken);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public void logout(Long userid) {
        User user = userService.getUser(userid);
        user.deleteRefreshToken();
        user.deleteAccessToken();
    }

    @Transactional
    public LoginResponseDto refresh(String token) {
        if (jwtUtil.isRefreshTokenValidate(token)) {
            String username = jwtUtil.getUsernameFromToken(token);

            User user = userService.getUser(username);
            String refreshToken = jwtService.generateRefreshToken(username);
            user.addRefreshToken(refreshToken);

            return new LoginResponseDto(
                    jwtService.generateAccessToken(username),
                    refreshToken
            );
        }

        return new LoginResponseDto("out", "out");
    }


}