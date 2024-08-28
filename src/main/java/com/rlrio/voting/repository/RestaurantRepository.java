package com.rlrio.voting.repository;

import com.rlrio.voting.model.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long>, JpaSpecificationExecutor<RestaurantEntity> {
}
