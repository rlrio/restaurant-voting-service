package com.rlrio.voting.service;

import com.rlrio.voting.config.security.util.SecurityUtil;
import com.rlrio.voting.controller.dto.vote.CountVoteDto;
import com.rlrio.voting.mapper.VoteMapper;
import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.UserEntity;
import com.rlrio.voting.model.VoteEntity;
import com.rlrio.voting.repository.VoteRepository;
import com.rlrio.voting.service.exception.NotFoundException;
import com.rlrio.voting.service.exception.VotingException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static java.text.MessageFormat.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final Clock clock;
    private final VoteRepository voteRepository;
    private final RestaurantService restaurantService;
    private final UserService userService;

    private static final int STOP_VOTE_HOUR = 11;

    @Transactional
    public void vote(Long restaurantId) {
        var username = SecurityUtil.getCurrentUsername();
        var user = userService.findByUsername(username);
        var restaurant = restaurantService.findById(restaurantId);
        voteRepository.findOneBy(user.getId(), null, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                .ifPresentOrElse(processVote(user, restaurant, false), () -> saveNewVote(user, restaurant));
    }

    @Transactional
    public void cancelVote(Long restaurantId) {
        var username = SecurityUtil.getCurrentUsername();
        var user = userService.findByUsername(username);
        var restaurant = restaurantService.findById(restaurantId);
        voteRepository.findOneBy(user.getId(), restaurantId, LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                .ifPresentOrElse(processVote(user, restaurant, true),
                        () -> { throw new NotFoundException(format("vote for the restaurant with id {0} was not found", restaurantId)); });
    }

    public List<CountVoteDto> countVotes(List<Long> restaurantIds) {
        if (CollectionUtils.isEmpty(restaurantIds)) {
            return emptyList();
        }
        var restaurantVoteCounts = voteRepository.countVotes(restaurantIds);
        return VoteMapper.INSTANCE.toDto(restaurantVoteCounts);
    }

    private void saveNewVote(UserEntity user, RestaurantEntity restaurant) {
        voteRepository.save(
                new VoteEntity()
                        .setRestaurant(restaurant)
                        .setUser(user)
                        .setVoteDateTime(now()));
    }

    private Consumer<VoteEntity> processVote(UserEntity user, RestaurantEntity restaurant, boolean isForCancelling) {
        return vote -> {
            validateVotingTime(isForCancelling ? "cancelled" : "changed");
            var isVotingForSameRestaurant = vote.getRestaurant().equals(restaurant);
            if (!isForCancelling && isVotingForSameRestaurant) {
                throw new VotingException(format("you have already voted for the restaurant with id {0} today", restaurant.getId()));
            }
            voteRepository.delete(vote);
            if (!isForCancelling) {
                saveNewVote(user, restaurant);
            }
        };
    }

    private void validateVotingTime(String actionMessage) {
        if (!canVoteAgain()) {
            throw new VotingException(format("vote cannot be {0} after {1} o''clock", actionMessage, STOP_VOTE_HOUR));
        }
    }

    private boolean canVoteAgain() {
        return !LocalTime.now(clock).isAfter(LocalTime.of(STOP_VOTE_HOUR, 0));
    }
}
