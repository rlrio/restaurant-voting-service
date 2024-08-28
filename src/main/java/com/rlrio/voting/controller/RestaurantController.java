package com.rlrio.voting.controller;

import com.rlrio.voting.controller.dto.PageResponse;
import com.rlrio.voting.controller.dto.restaurant.RestaurantFilter;
import com.rlrio.voting.controller.dto.restaurant.RestaurantFullInfo;
import com.rlrio.voting.service.RestaurantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "restaurant")
@RequestMapping("/restaurant/v1")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public PageResponse<RestaurantFullInfo> findRestaurants(RestaurantFilter filter) {
        return restaurantService.findAllByFilter(filter);
    }
}
