package com.rlrio.voting.controller.admin;

import com.rlrio.voting.controller.dto.restaurant.MenuDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantCreateOrUpdateDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantDto;
import com.rlrio.voting.controller.dto.restaurant.RestaurantFullInfo;
import com.rlrio.voting.service.RestaurantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "restaurant-admin")
@RequestMapping("/admin/restaurant/v1")
@RequiredArgsConstructor
public class AdminRestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public RestaurantDto create(@RequestBody @Valid RestaurantCreateOrUpdateDto dto) {
        return restaurantService.create(dto);
    }

    @PutMapping("/{id}")
    public RestaurantDto update(@PathVariable Long id, @RequestBody @Valid RestaurantCreateOrUpdateDto dto) {
        return restaurantService.update(id, dto);
    }

    @PostMapping("/{id}/menu")
    public RestaurantFullInfo setMenu(@PathVariable Long id, @RequestBody @Valid MenuDto menuDto) {
        return restaurantService.setMenu(id, menuDto);
    }
}
