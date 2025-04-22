package com.chandu.voting.service;

import com.chandu.voting.dto.PollDto;
import com.chandu.voting.dto.PollResultDto;
import com.chandu.voting.model.Participant;
import com.chandu.voting.model.Poll;
import com.chandu.voting.repository.PollRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public PollServiceImpl(PollRepository pollRepository, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.pollRepository = pollRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
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
        try {
            String key = "poll:" + pollId.toString();
            String cache = redisTemplate.opsForValue().get(key);
            if (cache == null) {
                Optional<Poll> savedPoll = pollRepository.findById(pollId);
                if (savedPoll.isPresent()) {
                    String pollJson = objectMapper.writeValueAsString(savedPoll.get());
                    redisTemplate.opsForValue().set(key, pollJson);
                }
                return savedPoll;
            }
            Poll poll = objectMapper.readValue(cache, Poll.class);
            return Optional.ofNullable(poll);
        } catch (JsonProcessingException exception) {
            log.error("Error deserializing poll from cache: {}", exception.getMessage(), exception);
            return Optional.empty();
        }
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
