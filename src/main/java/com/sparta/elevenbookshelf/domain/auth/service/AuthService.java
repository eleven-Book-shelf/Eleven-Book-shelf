package com.sparta.elevenbookshelf.domain.auth.service;


import com.sparta.elevenbookshelf.domain.auth.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.domain.auth.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserRequestDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final SocialService socialService;

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

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

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
        if (!jwtService.isRefreshTokenValidate(token)) {
           throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = userService.getUser(jwtService.getUsernameFromToken(token));

        String refreshToken = jwtService.generateRefreshToken(user);
        user.addRefreshToken(refreshToken);

        return new LoginResponseDto(
                jwtService.generateAccessToken(user), refreshToken
        );
    }

    @Transactional
    public void signOut(Long userId) {
        User user = userService.getUser(userId);

        if (user.getSocialId() != null) {
            String social = user.getUsername().split("_")[0];
            switch (social) {
                case "kakao" -> {
                    socialService.disconnectKakao(user.getOauthAccessToken());
                }
               case "google" -> {
                   socialService.disconnectGoogle(user.getOauthAccessToken());
                }
                case "naver" -> {
                    socialService.disconnectNaver(user.getOauthAccessToken());
                }
                default -> {
                    throw new IllegalStateException("Unexpected value: " + social);
                }
            }
        }

        user.signOut();
        user.deleteRefreshToken();


    }

}