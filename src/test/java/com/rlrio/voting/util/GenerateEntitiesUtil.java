package com.rlrio.voting.util;

import com.rlrio.voting.model.RestaurantEntity;
import com.rlrio.voting.model.Role;
import com.rlrio.voting.model.UserEntity;
import com.rlrio.voting.model.VoteEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class GenerateEntitiesUtil {

    public static UserEntity createUserEntity(String username, Role role) {
        return new UserEntity()
                .setUsername(username)
                .setPassword(RandomStringUtils.random(4))
                .setEmail(RandomStringUtils.random(10))
                .setRole(role);
    }

    public static RestaurantEntity createRestaurantEntity(String name) {
        return new RestaurantEntity().setName(name);
    }

    public static VoteEntity createVoteEntity(RestaurantEntity restaurant, UserEntity user) {
        return createVoteEntity(restaurant, user, LocalDateTime.now().minus(2, ChronoUnit.DAYS));
    }

    public static VoteEntity createVoteEntity(RestaurantEntity restaurant, UserEntity user, LocalDateTime dateTime) {
        return new VoteEntity()
                .setRestaurant(restaurant)
                .setVoteDateTime(dateTime)
                .setUser(user);
    }
}
