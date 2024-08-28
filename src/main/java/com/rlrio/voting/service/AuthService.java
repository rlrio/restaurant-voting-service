package com.rlrio.voting.service;

import com.rlrio.voting.config.security.CustomAuthenticationManager;
import com.rlrio.voting.config.security.JwtTokenProvider;
import com.rlrio.voting.config.security.util.SecurityUtil;
import com.rlrio.voting.controller.dto.auth.JwtTokenDto;
import com.rlrio.voting.controller.dto.auth.TokenDto;
import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomAuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserDto userDto) {
        userService.checkIfUserExists(userDto.getUsername());
        var user = UserMapper.INSTANCE.toUserEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userService.save(user);
    }

    public JwtTokenDto getJwtToken(TokenDto tokenDto) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        tokenDto.getUsername(),
                        tokenDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new JwtTokenDto()
                .setAccessToken(jwtTokenProvider.generateJwtToken(authentication))
                .setTokenType(SecurityUtil.JWT_TYPE)
                .setExpiresIn(SecurityUtil.JWT_EXPIRATION);
    }
}
