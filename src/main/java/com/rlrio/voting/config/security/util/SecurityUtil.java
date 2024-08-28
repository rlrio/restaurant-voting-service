package com.rlrio.voting.config.security.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class SecurityUtil {
    public static final String JWT_TYPE = "Bearer";
    public static final int JWT_EXPIRATION = 97200;

    public static String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            var userDetails = (UserDetails) auth.getPrincipal();
            return userDetails.getUsername();
        }
        return null;
    }
}
