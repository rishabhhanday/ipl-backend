package com.game.ipl.controller;

import com.game.ipl.model.UserCreationRequest;
import com.game.ipl.model.UserCreationResponse;
import com.game.ipl.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@Validated
@Slf4j
public class SignUpController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserCreationResponse> createUser(@Valid @RequestBody UserCreationRequest userCreationRequest) {
        log.info("Creating user request received : {}", userCreationRequest);
        UserCreationResponse userCreationResponse = userService.save(userCreationRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userCreationResponse);
    }
}
