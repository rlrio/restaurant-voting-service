package com.rlrio.voting.util;

import com.rlrio.voting.model.Role;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@UtilityClass
public class AuthUtil {

    public static void mockAuthentication(String username, Role role) {
        var mockUser = User
                .withUsername(username)
                .password(RandomStringUtils.random(4))
                .roles(role.name())
                .build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities()));
    }
}
