package com.chandu.voting.service;

import com.chandu.voting.dto.PollDto;
import com.chandu.voting.dto.PollResultDto;
import com.chandu.voting.model.Poll;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PollService {

    Poll create(PollDto pollDto);

    Optional<Poll> findById(UUID pollId);

    List<Poll> findAll();

    Map<String, Long>  findPollResultByPollId(UUID pollId);
}
