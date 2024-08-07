package com.sparta.elevenbookshelf.domain.oAuth2Token.entity;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "oauth2_token")
public class OAuth2Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth2_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String provider;
    private String principalName;
    private String refreshToken;

    @Builder
    public OAuth2Token(String provider, String principalName , String refreshToken) {
        this.provider = provider;
        this.principalName = principalName;
        this.refreshToken = refreshToken;
    }
}

