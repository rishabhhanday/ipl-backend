package com.game.ipl.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.game.ipl.entity.MatchInfo;
import com.game.ipl.model.UserMatchInfoResponse;
import com.game.ipl.services.JWTService;
import com.game.ipl.services.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
@Slf4j
public class MatchController {
    @Autowired
    private MatchService matchService;
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private JWTService jwtService;

    // done by admin , disable for prod
   /* @PostMapping("/match")
    ResponseEntity<MatchInfo> registerMatch(@RequestBody MatchInfo matchInfo) {
        log.info("Registering match information .");
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.save(matchInfo));
    }*/

    @GetMapping("/matches")
    ResponseEntity<List<UserMatchInfoResponse>> getMatchInfo(@RequestHeader("Authorization") String token) {
        Integer limit = 7;
        log.info("Getting {} match information ", limit);
        String username = jwtService.getUsername(token);

        return ResponseEntity.status(200).body(matchService.getNextMatchesByUsername(limit, username));
    }
}
