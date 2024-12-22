package com.rlrio.voting.controller.dto.restaurant;

import com.rlrio.voting.model.MenuItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuDto {
    @Valid
    @NotEmpty(message = "menu item list should not be empty")
    private List<MenuItem> menuItems;
}
