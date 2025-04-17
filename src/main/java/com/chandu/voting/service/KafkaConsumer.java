package com.chandu.voting.service;


import com.chandu.voting.dto.VoteDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class KafkaConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ExecutorService executorService;

    public KafkaConsumer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.executorService = Executors.newFixedThreadPool(2);
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


            executorService.submit(() -> {
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.error("Commit failed for offsets {}", offsets, exception);
                    } else {
                        log.info("Successfully committed offsets {}", offsets);
                    }
                });
            });

            executorService.submit(() -> {
                redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                    participantWithVotes.forEach((participantId, noOfVotes) -> {
                        String hashKey = "poll:" + participantWithPoll.get(participantId) + ":votes";
                        String field = "participant:" + participantId;
                        log.info("Incrementing votes for key: {}, field: {}, by: {}", hashKey, field, noOfVotes);
                        connection.hashCommands().hIncrBy(hashKey.getBytes(), field.getBytes(), noOfVotes);
                    });
                    return null;
                });

                log.info("Successfully executed Redis pipelining.");
            });

        } catch (Exception ex) {
            log.info("Exception : {}", ex.getStackTrace());
        }
    }
}
