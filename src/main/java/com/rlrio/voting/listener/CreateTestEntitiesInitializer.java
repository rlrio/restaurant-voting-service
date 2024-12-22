package com.rlrio.voting.listener;

import com.rlrio.voting.controller.dto.auth.UserDto;
import com.rlrio.voting.controller.dto.restaurant.MenuDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantCreateOrUpdateDto;
import com.rlrio.voting.model.MenuItem;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.model.UserEntity;
import com.rlrio.voting.service.AuthService;
import com.rlrio.voting.service.RestaurantService;
import com.rlrio.voting.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("ui-test")
public class CreateTestEntitiesInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final AuthService authService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createTestUsers();
        var restaurantId = createTestRestaurant();
        createTestMenu(restaurantId);
    }

    private void createTestUsers() {
        authService.register(new UserDto()
                .setEmail("test@mail.com")
                .setPassword("test")
                .setUsername("test"));
        authService.register(new UserDto()
                .setEmail("testAdmin@mail.com")
                .setPassword("testAdmin")
                .setUsername("testAdmin"));
        var admin = userService.findByUsername("testAdmin");
        userService.save(admin.setRole(Role.ADMIN));
    }

    private Long createTestRestaurant() {
        return restaurantService.create(new RestaurantCreateOrUpdateDto().setName("testRestaurant")).getId();
    }

    private void createTestMenu(Long restaurantId) {
        restaurantService.setMenu(
                restaurantId,
                new MenuDto().setMenuItems(List.of(
                        new MenuItem().setPrice(new BigDecimal("10")).setName("testDish"))));
    }
}
