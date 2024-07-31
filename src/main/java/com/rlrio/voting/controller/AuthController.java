package com.rlrio.voting.controller;

import com.rlrio.voting.config.security.JwtTokenProvider;
import com.rlrio.voting.config.security.util.SecurityUtil;
import com.rlrio.voting.controller.dto.auth.JwtTokenDto;
import com.rlrio.voting.controller.dto.auth.TokenDto;
import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "auth")
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid UserDto user) {
        userService.register(user);
    }

    @PostMapping("/token")
    public JwtTokenDto getToken(@RequestBody @Valid TokenDto tokenDto) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        tokenDto.getUsername(),
                        tokenDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new JwtTokenDto()
                .setAccessToken(jwtTokenProvider.generateJwtToken(authentication))
                .setTokenType(SecurityUtil.JWT_TYPE)
                .setExpiresIn(SecurityUtil.JWT_EXPIRATION);
    }
}
