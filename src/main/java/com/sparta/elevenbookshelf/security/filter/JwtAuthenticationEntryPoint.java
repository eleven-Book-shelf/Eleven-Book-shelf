package com.sparta.elevenbookshelf.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
         String message = jwtUtil.getErrorMessage(request.getHeader("Authorization"));

         response.setStatus(HttpStatus.FORBIDDEN.value());
    }
}
