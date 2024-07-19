package com.sparta.elevenbookshelf.security.oauth2.userinfo;

import java.util.Map;

public interface OAuth2UserInfo {

    String getProviderId(Map<String, Object> attributes);

    String getEmailFromAttributes(Map<String, Object> attributes);

    String getNameFromAttributes(Map<String, Object> attributes);

}
