package com.chandu.voting.service;


import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.Participant;
import com.chandu.voting.model.Poll;
import com.chandu.voting.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService{

    @Value("${constants.votes-topic}")
    private String VOTE_TOPIC;

    public final VoteRepository voteRepository;
    public final PollService pollService;
    public final ParticipantService participantService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, VoteDto> kafkaTemplate;


    public VoteServiceImpl(VoteRepository voteRepository, PollService pollService, ParticipantService participantService, RedisTemplate<String, Object> redisTemplate, KafkaTemplate<String, VoteDto> kafkaTemplate) {
        this.voteRepository = voteRepository;
        this.pollService = pollService;
        this.participantService = participantService;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public void vote(VoteDto voteDto) {
        Participant participant = participantService.findById(voteDto.getParticipantId()).orElseThrow(() -> new IllegalArgumentException("Participant not found!"));
        Poll poll = pollService.findById(voteDto.getPollId()).orElseThrow(() -> new IllegalArgumentException("Poll not found!"));
        kafkaTemplate.send(VOTE_TOPIC, voteDto);
    }

}
