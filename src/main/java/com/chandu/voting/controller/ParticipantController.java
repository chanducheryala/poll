package com.chandu.voting.controller;

import com.chandu.voting.model.Participant;
import com.chandu.voting.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @GetMapping("/participants/{pollId}")
    public ResponseEntity<List<Participant>> findAllByPollId(@PathVariable("pollId")UUID pollId) {
        return new ResponseEntity<List<Participant>>(participantService.findAllByPollId(pollId), HttpStatus.OK);
    }

}
