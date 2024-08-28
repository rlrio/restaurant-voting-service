package com.rlrio.voting.controller.dto.restaurant;

import com.rlrio.voting.model.MenuItem;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestaurantFullInfo {
    private Long id;
    private String name;
    private List<MenuItem> menuItems;
}
