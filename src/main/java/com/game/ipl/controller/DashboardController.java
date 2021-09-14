package com.game.ipl.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.model.DashboardResponse;
import com.game.ipl.services.DashboardService;
import com.game.ipl.services.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Slf4j
@Controller
public class DashboardController {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getUserResult(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsername(token);
        return ResponseEntity.ok(dashboardService.getDashboardResponse(username));
    }
}
