package com.rlrio.voting.controller;

import com.rlrio.voting.controller.dto.auth.JwtTokenDto;
import com.rlrio.voting.controller.dto.auth.TokenDto;
import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "auth")
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid UserDto user) {
        authService.register(user);
    }

    @PostMapping("/token")
    public JwtTokenDto getToken(@RequestBody @Valid TokenDto tokenDto) {
        return authService.getJwtToken(tokenDto);
    }
}
