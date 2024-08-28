package com.rlrio.voting.controller.dto.restaurant;

import com.rlrio.voting.model.MenuItem;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuDto {
    private List<MenuItem> menuItems;
}
