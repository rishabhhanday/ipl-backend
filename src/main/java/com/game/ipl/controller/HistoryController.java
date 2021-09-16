package com.game.ipl.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.game.ipl.entity.MatchInfo;
import com.game.ipl.model.HistoryResponse;
import com.game.ipl.services.HistoryService;
import com.game.ipl.services.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class HistoryController {
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private HistoryService historyService;

    @GetMapping("/history/matches")
    public ResponseEntity<List<MatchInfo>> getMatchDetails(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsername(token);

        log.info("token was parsed , {}", username);
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val2", new AttributeValue().withS(Instant.now().toString()));
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("matchOn < :val2").withExpressionAttributeValues(eav);

        List<MatchInfo> matchInfos = mapper.scan(MatchInfo.class, scanExpression);
        return ResponseEntity.ok(matchInfos);
    }

    @GetMapping("/history/matches/{matchId}")
    public ResponseEntity<HistoryResponse> getMatchById(@PathVariable String matchId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsername(token);

        log.info("token was parsed , {}", username);
        return ResponseEntity.ok(historyService.getMatchVotingHistory(matchId));
    }
}
