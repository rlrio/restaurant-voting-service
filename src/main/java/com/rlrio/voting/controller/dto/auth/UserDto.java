package com.rlrio.voting.controller.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    @NotBlank(message = "username should not be blank")
    private String username;
    @NotBlank(message = "email should not be blank")
    @Email(message = "incorrect format of the email")
    private String email;
    @NotBlank(message = "password should not be blank")
    private String password;
}
