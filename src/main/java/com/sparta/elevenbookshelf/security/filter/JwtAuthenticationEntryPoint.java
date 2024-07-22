package com.sparta.elevenbookshelf.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.elevenbookshelf.common.CommonErrorResponse;
import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
         String message = jwtUtil.getErrorMessage(request.getHeader("Authorization"));

         response.setStatus(HttpStatus.FORBIDDEN.value());
         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
         response.setCharacterEncoding(StandardCharsets.UTF_8.name());

         objectMapper.registerModule(new JavaTimeModule());

         response.getWriter().write(objectMapper.writeValueAsString(
                 CommonErrorResponse.builder()
                         .msg(message)
                         .status(HttpStatus.FORBIDDEN.value())
                         .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                         .path(request.getRequestURI())
                         .timestamp(LocalDateTime.now())
                         .build()
                )
         );
    }
}
