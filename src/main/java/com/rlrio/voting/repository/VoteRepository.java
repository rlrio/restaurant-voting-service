package com.rlrio.voting.repository;

import com.rlrio.voting.model.VoteEntity;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {

    @Query("select v from VoteEntity v " +
            "where v.user.id = :userId " +
            "and (:restaurantId is null or v.restaurant.id = :restaurantId) " +
            "and v.voteDateTime >= :dateTime")
    Optional<VoteEntity> findOneBy(@Param("userId") Long userId,
                                   @Nullable @Param("restaurantId") Long restaurantId,
                                   @Param("dateTime") LocalDateTime dateTime);

    List<VoteEntity> findAllByRestaurantIdIn(List<Long> restaurantIds);
}
