package com.chandu.voting.service;


import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.Participant;
import com.chandu.voting.model.Poll;
import com.chandu.voting.model.Vote;
import com.chandu.voting.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteServiceImpl implements VoteService{

    @Value("${constants.votes-topic}")
    private String VOTE_TOPIC;

    public final VoteRepository voteRepository;
    public final PollService pollService;
    public final ParticipantService participantService;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, VoteDto> kafkaTemplate;


    public VoteServiceImpl(VoteRepository voteRepository, PollService pollService, ParticipantService participantService, RedisTemplate<String, String> redisTemplate, KafkaTemplate<String, VoteDto> kafkaTemplate) {
        this.voteRepository = voteRepository;
        this.pollService = pollService;
        this.participantService = participantService;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public void vote(VoteDto voteDto) {
        kafkaTemplate.send(VOTE_TOPIC, voteDto);
    }

    @Override
    public List<Vote> convertToVotes(List<VoteDto> voteDtos) {
        return voteDtos.parallelStream().map(vote -> {
            Participant participant = participantService.findById(vote.getParticipantId()).orElseThrow(() -> new IllegalArgumentException("Participant not found!"));
            Poll poll = pollService.findById(vote.getPollId()).orElseThrow(() -> new IllegalArgumentException("Poll not found!"));
            return Vote.builder().poll(poll).participant(participant).build();
        }).toList();
    }

    @Override
    public List<Vote> bulkInsert(List<Vote> votes) {
        return voteRepository.saveAll(votes);
    }


}
