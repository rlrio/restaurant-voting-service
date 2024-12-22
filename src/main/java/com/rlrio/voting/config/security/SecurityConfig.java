package com.rlrio.voting.config.security;

import com.rlrio.voting.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final VotingJwtAuthEntryPoint unauthorizedHandler;
    private final VotingAuthorizationFilter votingAuthorizationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .servletApi(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/v3/api-docs/**",
                                "/error",
                                "/health",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/public/**",
                                "/favicon.ico",
                                "/auth/v1/**",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/vote/**").hasRole(Role.USER.name())
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(policy -> policy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(votingAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
