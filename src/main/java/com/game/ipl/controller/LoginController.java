package com.game.ipl.controller;

import com.game.ipl.model.LoginRequest;
import com.game.ipl.model.LoginResponse;
import com.game.ipl.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Slf4j
@Controller
@Validated
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("STARTED LOGIN | username={}", loginRequest.getUsername());
        LoginResponse loginResponse = userService.login(loginRequest);
        log.info("ENDED LOGIN | username={}", loginRequest.getUsername());
        return ResponseEntity.status(200).body(loginResponse);
    }
}
