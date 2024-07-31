package com.rlrio.voting.service;

import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.mapper.UserMapper;
import com.rlrio.voting.repository.UserRepository;
import com.rlrio.voting.service.exception.VotingException;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new VotingException(MessageFormat.format("user {0} not found", username)));
        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    public void register(UserDto userDto) {
        userRepository.findByUsername(userDto.getUsername())
                .ifPresent(user -> { throw new VotingException(MessageFormat.format("user {0} already exists", user.getUsername())); });

        var user = UserMapper.INSTANCE.toUserEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
