package com.rlrio.voting.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageFilter {
    @Min(value = 0, message = "pageNumber should be greater or equal to 0")
    protected int pageNumber = 0;
    @Min(value = 0, message = "pageSize should be greater or equal to 0")
    @Max(value = 1000, message = "pageSize should not be greater than 1000")
    protected int pageSize = 50;
}
