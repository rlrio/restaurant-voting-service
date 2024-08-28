package com.rlrio.voting.service;

import com.rlrio.voting.config.security.util.SecurityUtil;
import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.UserEntity;
import com.rlrio.voting.model.VoteEntity;
import com.rlrio.voting.repository.VoteRepository;
import com.rlrio.voting.service.exception.VotingException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final RestaurantService restaurantService;
    private final UserService userService;

    private static final int STOP_VOTE_HOUR = 11;

    @Transactional
    public void vote(Long restaurantId) {
        var username = SecurityUtil.getCurrentUsername();
        var user = userService.findByUsername(username);
        var restaurant = restaurantService.findById(restaurantId);
        voteRepository.findOneBy(user.getId(), restaurantId,
                        LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
                .ifPresentOrElse(checkAndChangeVote(restaurant), () -> saveNewVote(user, restaurant));
    }

    private void saveNewVote(UserEntity user, RestaurantEntity restaurant) {
        voteRepository.save(
                new VoteEntity()
                        .setRestaurant(restaurant)
                        .setUser(user)
                        .setVoteDateTime(LocalDateTime.now()));
    }

    private Consumer<VoteEntity> checkAndChangeVote(RestaurantEntity restaurant) {
        return vote -> {
            if (!canVoteAgain()) {
                throw new VotingException(MessageFormat.format("vote cannot be changed after {0} o''clock", STOP_VOTE_HOUR));
            }
            vote.setRestaurant(restaurant)
                .setVoteDateTime(LocalDateTime.now());
            voteRepository.save(vote);
        };
    }

    private boolean canVoteAgain() {
        return !LocalTime.now().isAfter(LocalTime.of(STOP_VOTE_HOUR, 0));
    }
}
