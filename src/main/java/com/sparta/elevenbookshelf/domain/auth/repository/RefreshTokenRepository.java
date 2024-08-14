package com.sparta.elevenbookshelf.domain.auth.repository;

import com.sparta.elevenbookshelf.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
