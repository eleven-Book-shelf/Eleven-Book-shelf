package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.UserRequestDto;
import com.sparta.elevenbookshelf.dto.UserResponseDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private RestTemplate restTemplate;

    @Value("${SOCIAL_GOOGLE_REVOKE}")
    private String googleRevokeUrl;

    @Transactional
    public void signup(UserRequestDto req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTING_USER);
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .status(User.Status.NORMAL)
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void signOut(Long userId) {
        User user = getUser(userId);
        user.signOut();
        user.deleteRefreshToken();
    }

    public UserResponseDto getProfile(Long userId) {
        User user = getUser(userId);
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto editProfile(Long userId, String username) {
        User user = getUser(userId);
        user.updateProfile(username);
        return null;
    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

}
