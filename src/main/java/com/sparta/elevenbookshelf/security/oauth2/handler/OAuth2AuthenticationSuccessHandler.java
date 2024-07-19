package com.sparta.elevenbookshelf.security.oauth2.handler;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.elevenbookshelf.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();

        String accessJwt = jwtService.generateAccessToken(user.getUsername());
        String refreshJwt = jwtService.generateRefreshToken(user.getUsername());

        LoginResponseDto loginResponseDto = new LoginResponseDto(accessJwt, refreshJwt);

        response.setHeader("Authorization", accessJwt);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(loginResponseDto));

    }

}
