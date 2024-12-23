package com.rlrio.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rlrio.voting.controller.dto.vote.VoteDto;
import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.repository.RestaurantRepository;
import com.rlrio.voting.repository.UserRepository;
import com.rlrio.voting.repository.VoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.rlrio.voting.util.AuthUtil.mockAuthentication;
import static com.rlrio.voting.util.EntityGenerationUtil.createRestaurantEntity;
import static com.rlrio.voting.util.EntityGenerationUtil.createUserEntity;
import static com.rlrio.voting.util.EntityGenerationUtil.createVoteEntity;
import static java.text.MessageFormat.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @MockBean
    private Clock clock;

    @BeforeEach
    @AfterEach
    public void setUp() {
        voteRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    void testVoteFirstTimeForTheDay() throws Exception {
        mockAuthentication("user", Role.USER);
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
    void testCancelVoteBefore11AM() throws Exception {
        mockAuthentication("user", Role.USER);
        when(clock.instant()).thenReturn(Instant.parse("2024-12-22T07:30:00Z"));
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var userGiven = userRepository.save(createUserEntity("user", Role.USER));
        voteRepository.save(createVoteEntity(restaurantGiven, userGiven, LocalDateTime.now()));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurantGiven.getId());
        assertEquals(1, voteRepository.findAll().size());

        mockMvc.perform(post("/vote/v1/cancel")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()));

        var votesFromDb = voteRepository.findAll();
        assertEquals(0, votesFromDb.size());
    }

    @Test
    void testCancelVoteAfter11AM() throws Exception {
        mockAuthentication("user", Role.USER);
        when(clock.instant()).thenReturn(Instant.parse("2024-12-22T11:30:00Z"));
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var userGiven = userRepository.save(createUserEntity("user", Role.USER));
        voteRepository.save(createVoteEntity(restaurantGiven, userGiven, LocalDateTime.now()));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurantGiven.getId());
        assertEquals(1, voteRepository.findAll().size());

        mockMvc.perform(post("/vote/v1/cancel")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("vote cannot be cancelled after 11 o'clock"));

        var votesFromDb = voteRepository.findAll();
        assertEquals(1, votesFromDb.size());
        assertEquals("user", votesFromDb.get(0).getUser().getUsername());
        assertEquals(restaurantGiven, votesFromDb.get(0).getRestaurant());
    }

    @Test
    void testVoteForNonExistingRestaurant() throws Exception {
        mockAuthentication("user", Role.USER);
        userRepository.save(createUserEntity("user", Role.USER));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(1L);

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("restaurant with id 1 is not found"));

        var votesFromDb = voteRepository.findAll();
        assertEquals(0, votesFromDb.size());
    }

    @Test
    void testVoteSecondTimeForTheDayBefore11AM() throws Exception {
        mockAuthentication("user", Role.USER);
        when(clock.instant()).thenReturn(Instant.parse("2024-12-22T07:30:00Z"));
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var restaurant2Given = restaurantRepository.save(createRestaurantEntity("restaurant-test-2"));
        var userGiven = userRepository.save(createUserEntity("user", Role.USER));
        voteRepository.save(createVoteEntity(restaurantGiven, userGiven, LocalDateTime.now()));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurant2Given.getId());
        assertEquals(1, voteRepository.findAll().size());

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()));

        var votesFromDb = voteRepository.findAll();
        assertEquals(1, votesFromDb.size());
        assertEquals("user", votesFromDb.get(0).getUser().getUsername());
        assertEquals(restaurant2Given, votesFromDb.get(0).getRestaurant());
    }

    @Test
    void testVoteSecondTimeForTheDayForTheSameRestaurantBefore11AM() throws Exception {
        mockAuthentication("user", Role.USER);
        when(clock.instant()).thenReturn(Instant.parse("2024-12-22T07:30:00Z"));
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var userGiven = userRepository.save(createUserEntity("user", Role.USER));
        voteRepository.save(createVoteEntity(restaurantGiven, userGiven, LocalDateTime.now()));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurantGiven.getId());
        assertEquals(1, voteRepository.findAll().size());

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(format("you have already voted for the restaurant with id {0} today", restaurantGiven.getId())));

        var votesFromDb = voteRepository.findAll();
        assertEquals(1, votesFromDb.size());
    }

    @Test
    void testVoteSecondTimeForTheDayAfter11AM() throws Exception {
        mockAuthentication("user", Role.USER);
        when(clock.instant()).thenReturn(Instant.parse("2024-12-22T11:30:00Z"));
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var restaurant2Given = restaurantRepository.save(createRestaurantEntity("restaurant-test-2"));
        var userGiven = userRepository.save(createUserEntity("user", Role.USER));
        voteRepository.save(createVoteEntity(restaurantGiven, userGiven, LocalDateTime.now()));
        var voteDtoGiven = new VoteDto()
                .setRestaurantId(restaurant2Given.getId());
        assertEquals(1, voteRepository.findAll().size());

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("vote cannot be changed after 11 o'clock"));

        var votesFromDb = voteRepository.findAll();
        assertEquals(1, votesFromDb.size());
        assertEquals("user", votesFromDb.get(0).getUser().getUsername());
        assertEquals(restaurantGiven, votesFromDb.get(0).getRestaurant());
    }

    @Test
    void testVoteWithoutAuthentication() throws Exception {
        var voteDtoGiven = new VoteDto().setRestaurantId(1L);

        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testVoteAsAdmin() throws Exception {
        mockAuthentication("admin", Role.ADMIN);
        var voteDtoGiven = new VoteDto().setRestaurantId(1L);
        mockMvc.perform(post("/vote/v1")
                        .content(objectMapper.writeValueAsString(voteDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCountVotes() throws Exception {
        mockAuthentication("user", Role.USER);
        createRestaurantWithVotes("restaurant-test", List.of("user1", "user2"));
        createRestaurantWithVotes("restaurant-test-2", List.of("user3", "user4", "user5"));
        var restaurantIds = restaurantRepository.findAll().stream()
                .map(RestaurantEntity::getId)
                .sorted()
                .toList();

        mockMvc.perform(post("/vote/v1/count")
                        .content(objectMapper.writeValueAsString(restaurantIds))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantId").value(restaurantIds.get(0)))
                .andExpect(jsonPath("$[0].votes").value(2))
                .andExpect(jsonPath("$[1].restaurantId").value(restaurantIds.get(1)))
                .andExpect(jsonPath("$[1].votes").value(3));
    }

    @Test
    void testCountVotesEmptyRequestBody() throws Exception {
        mockAuthentication("user", Role.USER);
        mockMvc.perform(post("/vote/v1/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCountVotesInvalidRequestBody() throws Exception {
        mockAuthentication("user", Role.USER);
        mockMvc.perform(post("/vote/v1/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCountVotes_NoVotesFound() throws Exception {
        mockAuthentication("user", Role.USER);
        List<Long> restaurantIds = List.of(3L);

        mockMvc.perform(post("/vote/v1/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantIds)))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    private void createRestaurantWithVotes(String restaurantName, List<String> usernames) {
        var restaurant = restaurantRepository.save(createRestaurantEntity(restaurantName));
        usernames.forEach(username -> {
            var user = userRepository.save(createUserEntity(username, Role.USER));
            voteRepository.save(createVoteEntity(restaurant, user, LocalDateTime.now()));
        });
    }
}