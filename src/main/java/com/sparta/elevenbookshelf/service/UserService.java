package com.sparta.elevenbookshelf.service;

import com.sparta.elevenbookshelf.dto.UserRequestDto;
import com.sparta.elevenbookshelf.dto.UserResponseDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserRequestDto req) {
    @Transactional
    public UserResponseDto signup(UserRequestDto req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTING_USER);
        }

        User user = User.builder()
                .username(req.getUsername())
//                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .status(User.Status.NORMAL)
                .role(User.Role.ADMIN)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void OAuth2login(Long userId,String accessToken ,String refreshJwt) {
        User user = getUser(userId);
        user.addRefreshToken(refreshJwt);
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

    private User getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private User getUser(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }


}
