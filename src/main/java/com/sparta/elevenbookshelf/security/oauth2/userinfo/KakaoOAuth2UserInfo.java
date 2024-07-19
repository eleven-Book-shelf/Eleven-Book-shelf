package com.sparta.elevenbookshelf.security.oauth2.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    @Override
    public String getProviderId(Map<String, Object> attributes) {

        return attributes.get("id").toString();

    }

    @Override
    public String getEmailFromAttributes(Map<String, Object> attributes) {

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) {

            throw new IllegalArgumentException("kakao_account is missing");

        }

        return (String) kakaoAccount.get("email");

    }

    @Override
    public String getNameFromAttributes(Map<String, Object> attributes) {

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {

            return "Unknown";

        }

        return (String) properties.getOrDefault("nickname", "Unknown");

    }
}
