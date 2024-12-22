package com.rlrio.voting.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

@Data
public class MenuItem {
    @NotBlank(message = "name cannot be blank")
    private String name;
    @NotNull(message = "price cannot be null")
    @DecimalMin(value = "0.01", message = "price should be greater than 0")
    @NumberFormat(pattern = "#,###.00")
    private BigDecimal price;
}
