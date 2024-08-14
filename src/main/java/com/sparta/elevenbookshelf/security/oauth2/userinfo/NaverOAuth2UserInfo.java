package com.sparta.elevenbookshelf.security.oauth2.userinfo;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    @Override
    public String getProviderId(Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return (String) response.get("id");

    }

    @Override
    public String getEmailFromAttributes(Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            throw new IllegalArgumentException("response is missing");
        }

        return (String) response.get("email");

    }

    @Override
    public String getNameFromAttributes(Map<String, Object> attributes) {

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return "Unknown";
        }

        return (String) response.getOrDefault("nickname", "Unknown");

    }
}
