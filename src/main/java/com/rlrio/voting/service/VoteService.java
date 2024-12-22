package com.rlrio.voting.service;

import com.rlrio.voting.config.security.util.SecurityUtil;
import com.rlrio.voting.controller.dto.vote.CountVoteDto;
import com.rlrio.voting.mapper.VoteMapper;
import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.UserEntity;
import com.rlrio.voting.model.VoteEntity;
import com.rlrio.voting.repository.VoteRepository;
import com.rlrio.voting.service.exception.VotingException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
        voteRepository.findOneBy(user.getId(),
                        LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                .ifPresentOrElse(checkAndChangeOrRemoveVote(user, restaurant),
                        () -> saveNewVote(user, restaurant));
    }

    public List<CountVoteDto> countVotes(List<Long> restaurantIds) {
        if (CollectionUtils.isEmpty(restaurantIds)) {
            return emptyList();
        }
        var votesToRestaurantId = voteRepository.findAllByRestaurantIdIn(restaurantIds).stream()
                .collect(Collectors.groupingBy(it -> it.getRestaurant().getId()));

        return VoteMapper.INSTANCE.toDto(votesToRestaurantId);
    }

    private void saveNewVote(UserEntity user, RestaurantEntity restaurant) {
        voteRepository.save(
                new VoteEntity()
                        .setRestaurant(restaurant)
                        .setUser(user)
                        .setVoteDateTime(now()));
    }

    private Consumer<VoteEntity> checkAndChangeOrRemoveVote(UserEntity user, RestaurantEntity restaurant) {
        return vote -> {
            if (!canVoteAgain()) {
                var actionMessage = vote.getRestaurant().equals(restaurant) ? "removed" : "changed";
                throw new VotingException(format("vote cannot be {0} after {1} o''clock", actionMessage, STOP_VOTE_HOUR));
            }
            voteRepository.delete(vote);
            if (!vote.getRestaurant().equals(restaurant)) {
                saveNewVote(user, restaurant);
            }
        };
    }

    private boolean canVoteAgain() {
        return !LocalTime.now(clock).isAfter(LocalTime.of(STOP_VOTE_HOUR, 0));
    }
}
