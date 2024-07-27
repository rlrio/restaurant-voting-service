package com.rlrio.voting.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class MenuItem {
    private String name;
    private BigDecimal price;
}
