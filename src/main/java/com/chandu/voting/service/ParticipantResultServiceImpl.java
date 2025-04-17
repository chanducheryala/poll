package com.chandu.voting.service;

import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.ParticipantResult;
import com.chandu.voting.repository.ParticipantResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ParticipantResultServiceImpl implements ParticipantResultService{

    @Autowired
    private ParticipantResultRepository participantResultRepository;


    @Override
    public void add(VoteDto voteDto) {

    }
}
