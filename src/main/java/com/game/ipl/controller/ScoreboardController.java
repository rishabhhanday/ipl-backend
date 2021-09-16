package com.game.ipl.controller;

import com.game.ipl.model.ScoreboardResponse;
import com.game.ipl.services.JWTService;
import com.game.ipl.services.ScoreboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@Slf4j
public class ScoreboardController {
    @Autowired
    private ScoreboardService scoreboardService;
    @Autowired
    private JWTService jwtService;

    @GetMapping("/leaderboard")
    ResponseEntity<ScoreboardResponse> getScores(@RequestHeader("Authorization") String token) {
        log.info("Getting scoreboard data.,{}", jwtService.getUsername(token));
        return ResponseEntity.ok(scoreboardService.getScore());
    }
}
