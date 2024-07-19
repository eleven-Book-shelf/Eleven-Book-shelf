package com.sparta.elevenbookshelf.security.oauth2.service;




import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import com.sparta.elevenbookshelf.security.config.PasswordEncorderConfig;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Oauth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncorderConfig encoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo;

        if ("google".equals(provider)) {
            userInfo = new GoogleOAuth2UserInfo();
        } else if ("naver".equals(provider)) {
            userInfo = new NaverOAuth2UserInfo();
        } else if ("kakao".equals(provider)) {
            userInfo = new KakaoOAuth2UserInfo();
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        String socialId = userInfo.getProviderId(oAuth2User.getAttributes());
        Optional<User> optionalUser = userRepository.findBySocialId(socialId);
        User user;

        if (optionalUser.isEmpty()) {
            user = User.builder()
                    .username(provider + "_" + socialId)
                    .password(encoder.passwordEncoder().encode("default_password"))
                    .email(userInfo.getEmailFromAttributes(oAuth2User.getAttributes()))
                    .socialId(socialId)
                    .role(User.Role.USER)
                    .status(User.Status.NORMAL)
                    .build();
            userRepository.save(user);
        }
        else {
            user = optionalUser.get();
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return userPrincipal;

    }
}
