package com.sparta.elevenbookshelf.security.oauth2.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import com.sparta.elevenbookshelf.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("onAuthenticationSuccess 실행됨");
        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        authService.OAuth2login(user.getId(), accessToken ,refreshToken);
        response.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        String redirectUrl = allowedOrigins+"/auth/callback?Authorization=" + accessToken;
        response.sendRedirect(redirectUrl);
    }

}
