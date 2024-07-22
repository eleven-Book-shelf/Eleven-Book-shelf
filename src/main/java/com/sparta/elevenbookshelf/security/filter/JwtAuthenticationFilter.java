package com.sparta.elevenbookshelf.security.filter;

import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import com.sparta.elevenbookshelf.security.principal.UserDetailsServiceImpl;
import com.sparta.elevenbookshelf.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("doFilterInternal 실행");
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("doFilterInternal accessToken 가져오기 : " + accessToken);

        if (!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.isTokenValidate(accessToken)) {
            filterChain.doFilter(request, response);
        }

        validateToken(accessToken);
    }

    private void validateToken(String token) {
        log.info("validateToken 메서드 실행. 받은 토큰 : " + token);
        Claims claims = jwtUtil.extractAllClaims(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        log.info("doFilterInternal accessToken validateToken 검사 끝 : " + token);
    }

}
