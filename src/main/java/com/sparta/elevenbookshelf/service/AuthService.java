package com.sparta.elevenbookshelf.service;


import com.sparta.elevenbookshelf.dto.LoginRequestDto;
import com.sparta.elevenbookshelf.entity.User;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import com.sparta.elevenbookshelf.repository.userRepository.UserRepository;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword(),
                        null
                )
        );

        User user = getUsername(loginRequestDto);

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refeshToken = jwtService.generateRefreshToken(user.getUsername());

        user.addRefreshToken(refeshToken);
        return new LoginResponseDto(accessToken, refeshToken);
    }

    public void logout(User userid){
        User user = getUser(userid.getId());
        user.deleteRefreshToken();
    }

    @Transactional
    public String refresh(String header) {
        jwtUtil.isTokenValidate(header);
        Claims claims = jwtUtil.extractAllClaims(header);
        User user = getUser(claims.getSubject());
        if(jwtUtil.isRefreshTokenValidate(user.getUsername())) {
            return jwtService.generateRefreshToken(user.getUsername());
        }
        return null;
    }



    //::::::::::::::::::::::::// TOOL BOX  //:::::::::::::::::::::::://

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new BusinessException(ErrorCode.USERNAME_NOT_FOUND)
        );
    }

    private User getUsername(LoginRequestDto loginRequestDto) {
        return userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
    }


}
