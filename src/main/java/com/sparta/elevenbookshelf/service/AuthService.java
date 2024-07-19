package com.sparta.elevenbookshelf.service;


import com.sparta.elevenbookshelf.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.dto.LoginResponseDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    @Transactional
    public String login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword(),
                        null
                )
        );

        User user = getUsername(loginRequestDto);
        user.addRefreshToken(jwtService.generateRefreshToken(user.getUsername()));
        return jwtService.generateAccessToken(user.getUsername());
    }

    public void logout(User userid){
        User user = getUser(userid.getId());
        user.deleteRefreshToken();
    }




    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USERNAME_NOT_FOUND)
        );
    }

    private User getUsername(LoginRequestDto loginRequestDto) {
        return userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new BusinessException(ErrorCode.USERNAME_NOT_FOUND)
        );
    }



}
