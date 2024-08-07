package com.sparta.elevenbookshelf.security.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("onAuthenticationSuccess 실행됨");

        User user = ((UserPrincipal) authentication.getPrincipal()).getUser();
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        OAuth2refreshToken(request, authentication, user, refreshToken, accessToken);

        String redirectUrl = allowedOrigins + "/auth/callback?Authorization=" + accessToken + "VAV" +refreshToken;
        response.sendRedirect(redirectUrl);

    }

    private void OAuth2refreshToken(HttpServletRequest request, Authentication authentication, User user, String refreshToken, String accessToken) {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient(
                    oauth2Token.getAuthorizedClientRegistrationId(),
                    authentication,
                    request
            );

            if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
                saveRefreshToken(user.getUsername(), authorizedClient.getClientRegistration().getRegistrationId(), refreshToken, accessToken);
            }
        } else {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "Invalid OAuth2 authentication token");
        }
    }

    protected void saveRefreshToken(String principalName, String provider, String refreshToken, String accessToken) {
        User user = userRepository.findByUsername(principalName).orElseThrow(
                () -> new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "Invalid user"));

        user.addRefreshToken(jwtService.generateRefreshToken(user.getUsername()));
        user.addOauthRefreshToken(refreshToken);
        userRepository.save(user);
    }
}
