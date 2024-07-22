package com.sparta.elevenbookshelf.security.oauth2.userinfo;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    @Override
    public String getProviderId(Map<String, Object> attributes) {

        return (String) attributes.get("sub");

    }

    @Override
    public String getEmailFromAttributes(Map<String, Object> attributes) {

        return (String) attributes.get("email");

    }

    @Override
    public String getNameFromAttributes(Map<String, Object> attributes) {

        String name = (String) attributes.get("name");

        if (name == null) {
            return "Unknown";
        }

        return name;

    }
}
