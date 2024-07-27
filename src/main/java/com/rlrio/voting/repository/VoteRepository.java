package com.rlrio.voting.repository;

import com.rlrio.voting.model.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<VoteEntity, Long> {

}
