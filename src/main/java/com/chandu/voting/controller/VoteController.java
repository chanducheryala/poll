package com.chandu.voting.controller;

import com.chandu.voting.dto.VoteDto;
import com.chandu.voting.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/votes")
    public ResponseEntity<?> vote(@RequestBody VoteDto voteDto) {
        voteService.vote(voteDto);
        return ResponseEntity.noContent().build();
    }
}
