package com.sparta.elevenbookshelf.domain.user.service;

import com.sparta.elevenbookshelf.domain.auth.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserRequestDto;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
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

    @Transactional
    public Page<UserResponseDto> getUserPage(int page,int size, String sortBy, boolean asc) {

        Sort.Direction direction = asc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserResponseDto::new);
    }

    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    public User getUsername(LoginRequestDto loginRequestDto) {
        return userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }
}
