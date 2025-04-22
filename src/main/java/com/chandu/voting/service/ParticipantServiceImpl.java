package com.chandu.voting.service;

import com.chandu.voting.model.Participant;
import com.chandu.voting.repository.ParticipantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ParticipantServiceImpl implements ParticipantService{

    private final ParticipantRepository participantRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ParticipantServiceImpl(ParticipantRepository participantRepository, RedisTemplate<String, String> redisTemplate) {
        this.participantRepository = participantRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Participant> findById(UUID participantId) {
        try {
            String key = "participant:" + participantId.toString();

            String cache = redisTemplate.opsForValue().get(key);

            if (cache == null) {
                Optional<Participant> savedParticipant = participantRepository.findById(participantId);

                if (savedParticipant.isPresent()) {
                    String participantJson = objectMapper.writeValueAsString(savedParticipant.get());
                    redisTemplate.opsForValue().set(key, participantJson);
                }

                return savedParticipant;
            }

            Participant participant = objectMapper.readValue(cache, Participant.class);
            return Optional.ofNullable(participant);

        } catch (JsonProcessingException ex) {
            log.error("Error serializing/deserializing participant with ID {}: {}", participantId, ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    @Override
    public List<Participant> findAllByPollId(UUID pollId) {
        return participantRepository.findAllByPollId(pollId);
    }

}
