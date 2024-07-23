package com.sparta.elevenbookshelf.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.elevenbookshelf.security.filter.JwtAuthenticationEntryPoint;
import com.sparta.elevenbookshelf.security.filter.JwtAuthenticationFilter;
import com.sparta.elevenbookshelf.security.jwt.JwtService;
import com.sparta.elevenbookshelf.security.jwt.JwtUtil;
import com.sparta.elevenbookshelf.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.sparta.elevenbookshelf.security.principal.UserDetailsServiceImpl;
import com.sparta.elevenbookshelf.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j(topic = "SecurityConfig")
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        log.info("@Bean OAuth2AuthenticationSuccessHandler 실행");
        return new OAuth2AuthenticationSuccessHandler(jwtService, objectMapper,userService);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        log.info("@Bean authenticationManager 실행");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        log.info("@Bean jwtAuthenticationFilter 실행");
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        log.info("@Bean authenticationEntryPoint 실행");
        return new JwtAuthenticationEntryPoint(jwtUtil, objectMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        log.info("@Bean securityFilterChain 실행");
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint())
        );

        http.authorizeHttpRequests(request ->
                                           request

                                                   .requestMatchers("/auth/login").permitAll()
                                                   .requestMatchers("/user/signup").permitAll()
                                                   .requestMatchers("/user/email/**").permitAll()
                                                   .requestMatchers("/auth/reissue").permitAll()
                                                   .requestMatchers(HttpMethod.GET, "/boards/**").permitAll()
                                                   .requestMatchers(HttpMethod.GET,  "/comments/**").permitAll()
                                                   .requestMatchers("/login.html").permitAll()
                                                   .requestMatchers("/admin/**").hasRole("ADMIN")
                                                   .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
                .loginPage("/login.html")
                .successHandler(oAuth2AuthenticationSuccessHandler())
        );

        return http.build();
    }
}
