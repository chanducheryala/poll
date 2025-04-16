package com.chandu.voting.service;

import com.chandu.voting.model.Participant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantService {
    Optional<Participant> findById(UUID participantId);
    List<Participant> findAllByPollId(UUID pollId);
}
