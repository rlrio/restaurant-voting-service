package com.rlrio.voting.controller.dto.vote;

import lombok.Data;

@Data
public class CountVoteDto {
    private Long restaurantId;
    private Long votes;
}
