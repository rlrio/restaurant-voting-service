package com.rlrio.voting.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserDetailsService userDetailsService;
    private final AuthenticationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            if (!passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid username or password. Please try again.");
            }
        } catch (Exception e) {
            eventPublisher.publishAuthenticationFailure(new BadCredentialsException(e.getMessage()), authentication);
            throw e;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials());
    }
}
