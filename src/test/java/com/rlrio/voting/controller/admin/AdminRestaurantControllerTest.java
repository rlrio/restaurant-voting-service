package com.rlrio.voting.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rlrio.voting.controller.dto.restaurant.MenuDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantCreateOrUpdateDto;
import com.rlrio.voting.model.MenuItem;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.repository.MenuRepository;
import com.rlrio.voting.repository.RestaurantRepository;
import com.rlrio.voting.repository.UserRepository;
import java.math.BigDecimal;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdminRestaurantControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @AfterEach
    public void setUp() {
        menuRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateRestaurantForbiddenForUser() throws Exception {
        mockAuthentication("user", Role.USER);
        userRepository.save(createUserEntity("user", Role.USER));

        mockMvc.perform(post("/admin/restaurant/v1")
                        .content(objectMapper.writeValueAsString(new RestaurantCreateOrUpdateDto()
                                .setName("test-restaurant")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateRestaurant() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);
        assertEquals(0, restaurantRepository.findAll().size());
        var restaurantNameGiven = "test-restaurant";

        mockMvc.perform(post("/admin/restaurant/v1")
                        .content(objectMapper.writeValueAsString(new RestaurantCreateOrUpdateDto()
                                .setName(restaurantNameGiven)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(restaurantNameGiven));

        assertEquals(1, restaurantRepository.findAll().size());
    }

    @Test
    void testUpdateRestaurant() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);
        var restaurantNameGiven = "restaurant-test";
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity(restaurantNameGiven));
        var newRestaurantName = "new-restaurant-name";
        mockMvc.perform(put("/admin/restaurant/v1/" + restaurantGiven.getId())
                        .content(objectMapper.writeValueAsString(new RestaurantCreateOrUpdateDto()
                                .setName(newRestaurantName)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newRestaurantName))
                .andExpect(jsonPath("$.id").value(restaurantGiven.getId()));

        var restaurantsFromDb = restaurantRepository.findAll();
        assertEquals(1, restaurantsFromDb.size());
        assertEquals(newRestaurantName, restaurantsFromDb.get(0).getName());
    }

    @Test
    void testSetMenu() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var menuDtoGiven = new MenuDto()
                .setMenuItems(List.of(new MenuItem()
                        .setName("testDish")
                        .setPrice(new BigDecimal("10.00"))));
        mockMvc.perform(post("/admin/restaurant/v1/" + restaurantGiven.getId() + "/menu")
                        .content(objectMapper.writeValueAsString(menuDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(restaurantGiven.getName()))
                .andExpect(jsonPath("$.id").value(restaurantGiven.getId()))
                .andExpect(jsonPath("$.menuItems[0].name").value(menuDtoGiven.getMenuItems().get(0).getName()))
                .andExpect(jsonPath("$.menuItems[0].price").value(menuDtoGiven.getMenuItems().get(0).getPrice().doubleValue()));

        var menuFromDb = menuRepository.findByRestaurantId(restaurantGiven.getId());
        assertTrue(menuFromDb.isPresent());
        assertEquals(menuDtoGiven.getMenuItems().get(0).getName(), menuFromDb.get().getMenuItems().get(0).getName());
    }

    @Test
    void testUpdateNonExistentRestaurant() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);

        mockMvc.perform(put("/admin/restaurant/v1/999")
                        .content(objectMapper.writeValueAsString(new RestaurantCreateOrUpdateDto()
                                .setName("non-existent-restaurant")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSetMenuForNonExistentRestaurant() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);
        var menuDto = new MenuDto()
                .setMenuItems(List.of(new MenuItem()
                        .setName("testDish")
                        .setPrice(new BigDecimal("10.00"))));

        mockMvc.perform(post("/admin/restaurant/v1/999/menu")
                        .content(objectMapper.writeValueAsString(menuDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRestaurantWithInvalidName() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);

        mockMvc.perform(post("/admin/restaurant/v1")
                        .content(objectMapper.writeValueAsString(new RestaurantCreateOrUpdateDto().setName("")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name cannot be empty"));
    }

    @Test
    void testSetMenuWithNegativePrice() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var menuDtoGiven = new MenuDto()
                .setMenuItems(List.of(new MenuItem()
                        .setName("testDish")
                        .setPrice(new BigDecimal("-10.00"))));
        mockMvc.perform(post("/admin/restaurant/v1/" + restaurantGiven.getId() + "/menu")
                        .content(objectMapper.writeValueAsString(menuDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("price should be greater than 0"));
    }

    @Test
    void testSetMenuWithNullMenuItemList() throws Exception {
        userRepository.save(createUserEntity("admin", Role.ADMIN));
        mockAuthentication("admin", Role.ADMIN);
        var restaurantGiven = restaurantRepository.save(createRestaurantEntity("restaurant-test"));
        var menuDtoGiven = new MenuDto();
        mockMvc.perform(post("/admin/restaurant/v1/" + restaurantGiven.getId() + "/menu")
                        .content(objectMapper.writeValueAsString(menuDtoGiven))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("menu item list should not be empty"));
    }
}