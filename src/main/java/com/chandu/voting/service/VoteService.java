package com.chandu.voting.service;

import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.Vote;


public interface VoteService {
    void vote(VoteDto voteDto);
}
