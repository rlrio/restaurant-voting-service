package com.rlrio.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rlrio.voting.controller.dto.PageResponse;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.repository.RestaurantRepository;
import com.rlrio.voting.repository.UserRepository;
import com.rlrio.voting.repository.VoteRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.rlrio.voting.util.AuthUtil.mockAuthentication;
import static com.rlrio.voting.util.EntityGenerationUtil.createRestaurantEntity;
import static com.rlrio.voting.util.EntityGenerationUtil.createUserEntity;
import static com.rlrio.voting.util.EntityGenerationUtil.createVoteEntity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RestaurantControllerTest {
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
    public void testFindRestaurants() throws Exception {
        var restaurant = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var user1 = userRepository.save(createUserEntity("test", Role.USER));
        var user2 = userRepository.save(createUserEntity("test2", Role.USER));
        voteRepository.save(createVoteEntity(restaurant, user1));
        voteRepository.save(createVoteEntity(restaurant, user2));

        var bodyExpected = objectMapper.writeValueAsString(new PageResponse<>()
                .setTotalPages(1)
                .setTotalSize(1)
                .setContent(List.of(restaurant))
                .setPageNumber(0)
                .setPageSize(50));

        mockMvc.perform(get("/restaurant/v1").queryParam("minVotes", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(bodyExpected));
    }
}