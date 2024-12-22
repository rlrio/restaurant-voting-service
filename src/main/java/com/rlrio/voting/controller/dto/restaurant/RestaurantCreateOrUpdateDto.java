package com.rlrio.voting.controller.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestaurantCreateOrUpdateDto {
    @NotBlank(message = "name cannot be empty")
    private String name;
}
