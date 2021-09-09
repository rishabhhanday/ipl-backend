package com.game.ipl.controller;

import com.game.ipl.model.ScoreboardResponse;
import com.game.ipl.services.ScoreboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ScoreboardController {
    @Autowired
    private ScoreboardService scoreboardService;

    @GetMapping("/scoreboard")
    ResponseEntity<ScoreboardResponse> getScores() {
        log.info("Getting scoreboard data.");
        return ResponseEntity.ok(scoreboardService.getScore());
    }
}
