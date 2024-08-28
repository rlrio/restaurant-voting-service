package com.rlrio.voting.controller;

import com.rlrio.voting.controller.dto.vote.VoteDto;
import com.rlrio.voting.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "vote")
@RequestMapping("/vote/v1")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public void vote(@RequestBody @Valid VoteDto voteDto) {
        voteService.vote(voteDto.getRestaurantId());
    }
}
