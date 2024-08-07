package com.sparta.elevenbookshelf.domain.oAuth2Token.repository;

import com.sparta.elevenbookshelf.domain.oAuth2Token.entity.OAuth2Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2TokenRepository extends JpaRepository<OAuth2Token, String> {
}
