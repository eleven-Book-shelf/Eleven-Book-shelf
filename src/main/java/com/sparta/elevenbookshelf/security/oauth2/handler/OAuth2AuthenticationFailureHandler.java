package com.sparta.elevenbookshelf.security.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage = "로그인에 실패했습니다. 다시 시도해 주세요.";
        if (exception.getMessage() != null) {
            errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        }

        log.error("OAuth2 authentication failure: {}", exception.getMessage());

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/login/failure?error=" + errorMessage);
    }
}
