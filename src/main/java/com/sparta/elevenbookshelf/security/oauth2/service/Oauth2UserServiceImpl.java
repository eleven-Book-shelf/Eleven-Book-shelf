package com.sparta.elevenbookshelf.security.oauth2.service;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.security.SecurityConfig;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.sparta.elevenbookshelf.security.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.sparta.elevenbookshelf.security.oauth2.userinfo.NaverOAuth2UserInfo;
import com.sparta.elevenbookshelf.security.oauth2.userinfo.OAuth2UserInfo;
import com.sparta.elevenbookshelf.security.principal.UserDetailsServiceImpl;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserServiceImpl extends DefaultOAuth2UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final SecurityConfig encoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = getUserInfoByProvider(provider);

        String socialId = userInfo.getProviderId(oAuth2User.getAttributes());
        Optional<User> optionalUser = userRepository.findBySocialId(socialId);
        User user;

        if (optionalUser.isEmpty()) {
            user = User.builder()
                    .username(provider + "_" + socialId)
                    .nickname(userInfo.getNameFromAttributes(oAuth2User.getAttributes()))
                    .password(encoder.passwordEncoder().encode("default_password"))
                    .email(userInfo.getEmailFromAttributes(oAuth2User.getAttributes()))
                    .socialId(socialId)
                    .role(User.Role.USER)
                    .status(User.Status.NORMAL)
                    .build();

            userRepository.save(user);
        } else {
            user = optionalUser.get();
            if (user.getStatus() == User.Status.DELETED) {
                throw new OAuth2AuthenticationException(new OAuth2Error("user_deleted"), "탈퇴한 사용자 입니다.");
            }
            if (user.getStatus() == User.Status.BLOCKED) {
                throw new OAuth2AuthenticationException(new OAuth2Error("user_blocked"), "차단된 사용자 입니다.");
            }
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        user.addRefreshToken(jwtService.generateRefreshToken(provider + "_" + socialId));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return userPrincipal;
    }

    private OAuth2UserInfo getUserInfoByProvider(String provider){

        if ("google".equals(provider)) {
            return new GoogleOAuth2UserInfo();
        } else if ("naver".equals(provider)) {
            return new NaverOAuth2UserInfo();
        } else if ("kakao".equals(provider)) {
            return new KakaoOAuth2UserInfo();
        } else {
            throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_provider"), "Unsupported provider: " + provider);
        }
    }
}
