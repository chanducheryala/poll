package com.chandu.voting.repository;

import com.chandu.voting.model.ParticipantResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantResultRepository extends JpaRepository<ParticipantResult, UUID> {

    @Query(
            value = "SELECT * FROM participant_result pr WHERE pr.participant_id = :participantId AND pr.poll_id = :pollId",
            nativeQuery = true
    )
    Optional<ParticipantResult> findByParticipantIdAndPollId(@Param("participantId") UUID participantId, @Param("pollId") UUID pollId);
}
