package com.chandu.voting.service;

import com.chandu.voting.dto.PollDto;
import com.chandu.voting.dto.PollResultDto;
import com.chandu.voting.model.Participant;
import com.chandu.voting.model.Poll;
import com.chandu.voting.repository.PollRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;

    public PollServiceImpl(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    @Override
    @Transactional
    public Poll create(PollDto pollDto) {
        Poll poll = Poll.builder()
                .title(pollDto.getTitle())
                .build();
        List<Participant> participants = pollDto.getParticipants()
                .stream().map(participant -> {
                    return Participant.builder()
                            .name(participant.getName()).poll(poll).build();
                }).toList();
        poll.setParticipants(participants);
        Poll savedPoll = pollRepository.save(poll);
        log.info("saved poll {}", savedPoll);
        return savedPoll;
    }

    @Override
    public Optional<Poll> findById(UUID pollId) {
        return pollRepository.findById(pollId);
    }

    @Override
    public List<Poll> findAll() {
        return pollRepository.findAll();
    }

    @Override
    public List<PollResultDto> findPollResultByPollId(UUID pollId) {
        return pollRepository.findPollResultByPollId(pollId);
    }

}
