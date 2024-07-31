package com.rlrio.voting.controller.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JwtTokenDto {
    private String tokenType;
    private String accessToken;
    private int expiresIn;
}
