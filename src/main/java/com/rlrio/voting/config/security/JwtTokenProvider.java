package com.rlrio.voting.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.rlrio.voting.config.security.util.SecurityUtil.JWT_EXPIRATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private Key secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Jwts.SIG.HS256.key().random(new SecureRandom()).build();
    }

    public String generateJwtToken(Authentication authentication) {
        var userPrincipal = (UserDetails) authentication.getPrincipal();
        var claims = Map.of(
                userPrincipal.getUsername(),
                userPrincipal.getAuthorities().stream()
                        .map(Objects::toString)
                        .toList()
        );

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + JWT_EXPIRATION * 1000L))
                .subject(userPrincipal.getUsername())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .claims(claims)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseSignedClaims(getJwtFromRequest(token));
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature. Message: {}", e.getMessage(), e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token. Message: {}", e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token. Message: {}", e.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token. Message: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error with JWT token. Message: {}", e.getMessage(), e);
        }
        return false;
    }

    public String getUserFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private String getJwtFromRequest(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
