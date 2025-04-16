package com.chandu.voting.service;

import com.chandu.voting.model.Participant;
import com.chandu.voting.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParticipantServiceImpl implements ParticipantService{

    private final ParticipantRepository participantRepository;

    public ParticipantServiceImpl(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public Optional<Participant> findById(UUID participantId) {
        return participantRepository.findById(participantId);
    }

    @Override
    public List<Participant> findAllByPollId(UUID pollId) {
        return participantRepository.findAllByPollId(pollId);
    }

}
