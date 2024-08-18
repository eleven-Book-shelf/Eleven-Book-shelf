package com.sparta.elevenbookshelf.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.elevenbookshelf.domain.auth.service.RefreshTokenService;
import com.sparta.elevenbookshelf.domain.user.repository.UserRepository;
import com.sparta.elevenbookshelf.security.config.CustomAccessDeniedHandler;
import com.sparta.elevenbookshelf.security.filter.JwtAuthenticationEntryPoint;
import com.sparta.elevenbookshelf.security.filter.JwtAuthenticationFilter;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.sparta.elevenbookshelf.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.sparta.elevenbookshelf.security.principal.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j(topic = "SecurityConfig")
public class SecurityConfig {

    @Value("${CORS_ALLOWED_ORIGINS}")
    private String allowedOrigins;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("@Bean passwordEncoder 실행");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        log.info("@Bean authenticationManager 실행");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        log.info("@Bean OAuth2AuthenticationSuccessHandler 실행");
        return new OAuth2AuthenticationSuccessHandler(
                jwtService, refreshTokenService, userRepository, authorizedClientRepository);
    }

    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        log.info("@Bean OAuth2AuthenticationFailureHandler 실행");
        return new OAuth2AuthenticationFailureHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        log.info("@Bean jwtAuthenticationFilter 실행");
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        log.info("@Bean authenticationEntryPoint 실행");
        return new JwtAuthenticationEntryPoint(jwtService, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        log.info("@Bean securityFilterChain 실행");
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        http.authorizeHttpRequests(request ->
                                           request
                                                   .requestMatchers("/api/content/**").permitAll()
                                                   .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                   .requestMatchers("/api/user/signup").permitAll()
                                                   .requestMatchers("/api/auth/login").permitAll()
                                                   .requestMatchers("/api/contents/**").permitAll()
                                                   .requestMatchers("/api/hashtag/**").permitAll()
                                                   .requestMatchers("/api/boards/**").permitAll()
                                                   .requestMatchers("/api/auth/**").permitAll()
                                                   .requestMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                                                   .requestMatchers( HttpMethod.GET,"/api/hashtag").permitAll()
                                                   .anyRequest().authenticated()
//                                                   .anyRequest().permitAll()
        );


        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
                .loginPage(allowedOrigins +"/login")
                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureHandler(oAuth2AuthenticationFailureHandler())
        );

        return http.build();
    }
}

