package com.rlrio.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rlrio.voting.controller.dto.vote.VoteDto;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.repository.RestaurantRepository;
import com.rlrio.voting.repository.UserRepository;
import com.rlrio.voting.repository.VoteRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.rlrio.voting.util.AuthUtil.mockAuthentication;
import static com.rlrio.voting.util.EntityGenerationUtil.createRestaurantEntity;
import static com.rlrio.voting.util.EntityGenerationUtil.createUserEntity;
import static com.rlrio.voting.util.EntityGenerationUtil.createVoteEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class VoteControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @AfterEach
    public void setUp() {
        mockAuthentication("user", Role.USER);
        voteRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testVoteFirstTimeForTheDay() throws Exception {
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        userRepository.save(createUserEntity("user", Role.USER));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurantGiven.getId());
        assertEquals(0, voteRepository.findAll().size());

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var votesFromDb = voteRepository.findAll();
        assertEquals(1, votesFromDb.size());
        assertEquals("user", votesFromDb.get(0).getUser().getUsername());
        assertEquals(restaurantGiven, votesFromDb.get(0).getRestaurant());
    }

    @Test
    void testVoteSecondTimeForTheDay() throws Exception {
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var userGiven = userRepository.save(createUserEntity("user", Role.USER));
        voteRepository.save(createVoteEntity(restaurantGiven, userGiven, LocalDateTime.now()));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurantGiven.getId());
        assertEquals(1, voteRepository.findAll().size());

        var expectedStatus = LocalTime.now().isBefore(LocalTime.of(11, 0))
                ? HttpStatus.OK
                : HttpStatus.BAD_REQUEST;

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus.value()));

        var votesFromDb = voteRepository.findAll();
        assertEquals(1, votesFromDb.size());
        assertEquals("user", votesFromDb.get(0).getUser().getUsername());
        assertEquals(restaurantGiven, votesFromDb.get(0).getRestaurant());
    }

}