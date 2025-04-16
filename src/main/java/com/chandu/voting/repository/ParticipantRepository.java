package com.chandu.voting.repository;

import com.chandu.voting.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    @Query(
            value = "SELECT * FROM participant WHERE poll_id = :pollId",
            nativeQuery = true
    )
    List<Participant> findAllByPollId(@Param("pollId") UUID pollId);
}
