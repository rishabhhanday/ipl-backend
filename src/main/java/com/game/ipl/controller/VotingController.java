package com.game.ipl.controller;

import com.game.ipl.entity.VotingInfo;
import com.game.ipl.model.VotingRequest;
import com.game.ipl.services.JWTService;
import com.game.ipl.services.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class VotingController {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private VotingService votingService;

    @PostMapping("/vote")
    ResponseEntity<VotingInfo> vote(@RequestBody VotingRequest votingRequest, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsername(token);

        return ResponseEntity.status(201).body(votingService.vote(votingRequest, username));
    }
}
