package com.sparta.elevenbookshelf.domain.user.repository;

import com.sparta.elevenbookshelf.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findBySocialId(String socialId);
}
