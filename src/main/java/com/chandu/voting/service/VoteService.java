package com.chandu.voting.service;
import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.Vote;

import java.util.List;

public interface VoteService {
    void vote(VoteDto voteDto);
    List<Vote> convertToVotes(List<VoteDto> voteDtos);
    List<Vote> bulkInsert(List<Vote> votes);
}
