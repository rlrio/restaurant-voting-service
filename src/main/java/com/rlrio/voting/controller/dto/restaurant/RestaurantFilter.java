package com.rlrio.voting.controller.dto.restaurant;

import com.rlrio.voting.controller.dto.PageFilter;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;

@Getter
@Service
@ToString
public class RestaurantFilter extends PageFilter {
    private String nameStartsWith;
    private Long minVotes;
}
