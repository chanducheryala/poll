package com.chandu.voting.controller;


import com.chandu.voting.dto.PollDto;
import com.chandu.voting.model.Poll;
import com.chandu.voting.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PollController {

    @Autowired
    private PollService pollService;


    @PostMapping("/polls")
    public ResponseEntity<Poll> create(@RequestBody PollDto pollDto) {
        return new ResponseEntity<>(pollService.create(pollDto), HttpStatus.CREATED);
    }

    @GetMapping("/polls")
    public ResponseEntity<List<Poll>> findAll() {
        return new ResponseEntity<>(pollService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/polls/{pollId}")
    public ResponseEntity<Poll> findOne(@PathVariable("pollId") UUID pollId) {
        return new ResponseEntity<>(pollService.findById(pollId).get(), HttpStatus.OK);
    }

    @GetMapping("/polls/result/{pollId}")
    public ResponseEntity<Map<String, Long>> findPollResultByPollId(@PathVariable("pollId")UUID pollId) {
        return new ResponseEntity<>(pollService.findPollResultByPollId(pollId), HttpStatus.OK);
    }
}
