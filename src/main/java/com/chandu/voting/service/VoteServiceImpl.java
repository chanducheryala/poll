package com.chandu.voting.service;


import com.chandu.voting.dto.PollResultDto;
import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.Participant;
import com.chandu.voting.model.Poll;
import com.chandu.voting.model.Vote;
import com.chandu.voting.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VoteServiceImpl implements VoteService{

    public final VoteRepository voteRepository;
    public final PollService pollService;
    public final ParticipantService participantService;

    public VoteServiceImpl(VoteRepository voteRepository, PollService pollService, ParticipantService participantService) {
        this.voteRepository = voteRepository;
        this.pollService = pollService;
        this.participantService = participantService;
    }

    @Override
    @Transactional
    public Vote vote(VoteDto voteDto) {
        Participant participant = participantService.findById(voteDto.getParticipantId()).orElseThrow(() -> new IllegalArgumentException("Participant not found!"));
        Poll poll = pollService.findById(voteDto.getPollId()).orElseThrow(() -> new IllegalArgumentException("Poll not found!"));

        Vote vote = Vote.builder()
                .poll(poll)
                .participant(participant)
                .userId(voteDto.getUserId())
                .build();
        return voteRepository.save(vote);
    }

}
