package com.rlrio.voting.repository;

import com.rlrio.voting.model.MenuEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    Optional<MenuEntity> findByRestaurantId(Long restaurantId);
}
