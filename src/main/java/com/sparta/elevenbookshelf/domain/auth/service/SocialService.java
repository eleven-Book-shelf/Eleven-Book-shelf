package com.sparta.elevenbookshelf.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final RestTemplate restTemplate;

    @Value("${SOCIAL_KAKAO_CLIENT_ID}")
    private String kakaoApiKey;

    @Value("${SOCIAL_NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${SOCIAL_NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    private String naverTokenUri = "https://nid.naver.com/oauth2.0/token";

    @Value("${SOCIAL_KAKAO_REVOKE}")
    private  String kakaoUnlinkUrl;

    @Value("${SOCIAL_GOOGLE_REVOKE}")
    private String googleRevokeUrl;


    public ResponseEntity<String> disconnectKakao(String accessToken) {
        return disconnectSocialMedia(kakaoUnlinkUrl, accessToken, "카카오");
    }

    public ResponseEntity<String> disconnectNaver(String accessToken) {
        URI uri = UriComponentsBuilder.fromUriString(naverTokenUri)
                .queryParam("grant_type", "delete")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("access_token", accessToken)
                .queryParam("service_provider", "NAVER")
                .build()
                .toUri();

        return disconnectSocialMedia(uri, accessToken, "네이버");
    }

    public ResponseEntity<String> disconnectGoogle(String token) {
        URI uri = UriComponentsBuilder.fromUriString(googleRevokeUrl)
                .queryParam("token", token)
                .build()
                .toUri();

        return disconnectSocialMedia(uri, token, "구글");
    }


    private ResponseEntity<String> disconnectSocialMedia(String url, String accessToken, String provider) {
        try {
            URI uri = new URI(url);
            return disconnectSocialMedia(uri, accessToken, provider);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(3838).body("잘못된 URI 형식입니다.");
        }
    }

    private ResponseEntity<String> disconnectSocialMedia(URI uri, String accessToken, String provider) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(provider + " 연동 해제에 성공했습니다.");
            } else {
                return ResponseEntity.status(3838).body(provider + " 연동 해제 응답을 받지 못했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(3838).body(provider + " 연동 해제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
