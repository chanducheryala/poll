package com.chandu.voting.service;


import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.model.Vote;
import com.chandu.voting.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class KafkaConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    private final VoteService voteService;


    public KafkaConsumer(RedisTemplate<String, String> redisTemplate, VoteService voteService) {
        this.redisTemplate = redisTemplate;
        this.voteService = voteService;
    }


    @KafkaListener(topics = "${constants.votes-topic}", groupId = "votes-consumer-group")
    public void listen(List<VoteDto> voteDtos, Consumer<String, VoteDto> consumer) {
        try {
            log.info("consumed {} votes ", voteDtos.size());
            ConcurrentHashMap<String, Long> participantWithVotes = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, String> participantWithPoll = new ConcurrentHashMap<>();

            voteDtos.parallelStream().forEach(vote -> {
                participantWithVotes.merge(vote.getParticipantId().toString(), 1L, Long::sum);
                participantWithPoll.putIfAbsent(vote.getParticipantId().toString(), vote.getPollId().toString());
            });


            consumer.commitAsync((offsets, exception) -> {
                if (exception != null) {
                    log.error("Commit failed for offsets {}", offsets, exception);
                } else {
                    log.info("Successfully committed offsets {}", offsets);
                }
            });

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                participantWithVotes.forEach((participantId, noOfVotes) -> {
                    String hashKey = "poll:" + participantWithPoll.get(participantId) + ":votes";
                    String field = "participant:" + participantId;
                    log.info("Incrementing votes for key: {}, field: {}, by: {}", hashKey, field, noOfVotes);
                    connection.hashCommands().hIncrBy(hashKey.getBytes(), field.getBytes(), noOfVotes);
                });
                return null;
            });

            List<Vote> votes = voteService.convertToVotes(voteDtos);
            List<Vote> savedVotes = voteService.bulkInsert(votes);
            log.info("savedVotes {}", savedVotes.size());
            log.info("Successfully executed Redis pipelining.");

        } catch (Exception ex) {
            log.info("Exception : {}", ex.getStackTrace());
        }
    }
}
