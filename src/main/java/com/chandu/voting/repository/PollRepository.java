package com.chandu.voting.repository;

import com.chandu.voting.dto.PollResultDto;
import com.chandu.voting.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {

    @Query("SELECT new com.chandu.voting.dto.PollResultDto(p.id, p.name, COUNT(v)) " +
            "FROM Poll poll " +
            "JOIN poll.participants p " +
            "LEFT JOIN Vote v ON v.participant.id = p.id AND v.poll.id = :pollId " +
            "WHERE poll.id = :pollId " +
            "GROUP BY p.id")
    List<PollResultDto> findPollResultByPollId(@Param("pollId") UUID pollId);

}
