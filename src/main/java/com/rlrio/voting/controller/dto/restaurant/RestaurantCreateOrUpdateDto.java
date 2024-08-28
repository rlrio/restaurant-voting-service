package com.rlrio.voting.controller.dto.restaurant;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestaurantCreateOrUpdateDto {
    @NotNull(message = "name cannot be null")
    private String name;
}
