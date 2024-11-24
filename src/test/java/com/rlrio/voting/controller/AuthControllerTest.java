package com.rlrio.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rlrio.voting.controller.dto.auth.TokenDto;
import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.rlrio.voting.util.EntityGenerationUtil.createUserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser() throws Exception {
        var userDtoGiven = new UserDto()
                .setUsername("test-user")
                .setEmail("user@test.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10));
        assertEquals(0, userRepository.findAll().size());

        mockMvc.perform(post("/auth/v1/register")
                        .content(objectMapper.writeValueAsString(userDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var usersFromDb = userRepository.findAll();
        assertEquals(1, usersFromDb.size());
    }

    @Test
    void testTryRegisterExistedUser() throws Exception {
        userRepository.save(createUserEntity("test-user", Role.USER));
        var userDtoGiven = new UserDto()
                .setUsername("test-user")
                .setEmail("user@test.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10));

        mockMvc.perform(post("/auth/v1/register")
                        .content(objectMapper.writeValueAsString(userDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetToken() throws Exception {
        var userDtoGiven = new UserDto()
                .setUsername("test-user")
                .setEmail("user@test.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10));
        assertEquals(0, userRepository.findAll().size());

        mockMvc.perform(post("/auth/v1/register")
                        .content(objectMapper.writeValueAsString(userDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var tokenDtoGiven = new TokenDto()
                .setUsername(userDtoGiven.getUsername())
                .setPassword(userDtoGiven.getPassword());

        mockMvc.perform(post("/auth/v1/token")
                        .content(objectMapper.writeValueAsString(tokenDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void testGetTokenBadCredentials() throws Exception {
        var userDtoGiven = new UserDto()
                .setUsername("test-user")
                .setEmail("user@test.com")
                .setPassword(RandomStringUtils.randomAlphabetic(10));
        assertEquals(0, userRepository.findAll().size());

        mockMvc.perform(post("/auth/v1/register")
                        .content(objectMapper.writeValueAsString(userDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var tokenDtoGiven = new TokenDto()
                .setUsername(userDtoGiven.getUsername())
                .setPassword(RandomStringUtils.randomAlphabetic(10));

        mockMvc.perform(post("/auth/v1/token")
                        .content(objectMapper.writeValueAsString(tokenDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password. Please try again."));
    }
}