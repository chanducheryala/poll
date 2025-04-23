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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ParticipantService participantService;

    public PollServiceImpl(PollRepository pollRepository, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, ParticipantService participantService) {
        this.pollRepository = pollRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.participantService = participantService;
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
    public Map<String, Long> findPollResultByPollId(UUID pollId) {

        String leaderboardKey = "poll:" + pollId + ":leaderboard";

        Set<ZSetOperations.TypedTuple<String>> topParticipantsWithScores =
                redisTemplate.opsForZSet().reverseRangeWithScores(leaderboardKey, 0, -1);

        Map<String, Long> scoreBoard = new LinkedHashMap<>(); // LinkedHashMap to maintain order

        if (topParticipantsWithScores != null) {
            for (ZSetOperations.TypedTuple<String> tuple : topParticipantsWithScores) {
                String participantKey = tuple.getValue(); // e.g., "participant:456"
                String participantId = participantKey.replace("participant:", "");

                Participant participant = participantService.findById(UUID.fromString(participantId))
                        .orElseThrow(() -> new NoSuchElementException("Participant not found!"));

                scoreBoard.put(participant.getName(), tuple.getScore().longValue());
            }
        }

        return scoreBoard;
    }

}
