package com.rlrio.voting.controller.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenDto {
    @NotBlank(message = "username should not be blank")
    private String username;
    @NotBlank(message = "password should not be blank")
    private String password;
}
