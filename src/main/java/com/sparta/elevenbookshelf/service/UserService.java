package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.UserRequestDto;
import com.sparta.elevenbookshelf.dto.UserResponseDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto signup(UserRequestDto req) {

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
        return  UserResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
    
    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    private User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

}
