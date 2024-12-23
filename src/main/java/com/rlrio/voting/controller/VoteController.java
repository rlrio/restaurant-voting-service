package com.rlrio.voting.controller;

import com.rlrio.voting.controller.dto.vote.CountVoteDto;
import com.rlrio.voting.controller.dto.vote.VoteDto;
import com.rlrio.voting.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "vote")
@RequestMapping("/vote/v1")
@RequiredArgsConstructor
public class VoteController extends AbstractController {
    private final VoteService voteService;

    @Operation(summary = "Vote for a restaurant", description = "Allows a user to vote for a specific restaurant.")
    @PostMapping
    public void vote(@RequestBody @Valid VoteDto voteDto) {
        voteService.vote(voteDto.getRestaurantId());
    }

    @Operation(summary = "Vote for a restaurant", description = "Allows a user to vote for a specific restaurant.")
    @PostMapping("/cancel")
    public void cancelVote(@RequestBody @Valid VoteDto voteDto) {
        voteService.cancelVote(voteDto.getRestaurantId());
    }

    @Operation(summary = "Find number of votes for restaurants", description = "Allows to get votes by restaurantIds")
    @PostMapping("/count")
    public List<CountVoteDto> countVotes(@RequestBody @NotEmpty @Valid List<Long> restaurantIds) {
        return voteService.countVotes(restaurantIds);
    }
}
