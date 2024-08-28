package com.rlrio.voting.service;

import com.rlrio.voting.model.UserEntity;
import com.rlrio.voting.repository.UserRepository;
import com.rlrio.voting.service.exception.VotingException;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = findByUsername(username);
        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new VotingException(MessageFormat.format("user {0} is not found", username)));
    }

    public void checkIfUserExists(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> { throw new VotingException(
                        MessageFormat.format("user {0} already exists", username));
                });
    }

    public void save(UserEntity user) {
        userRepository.save(user);
    }
}
