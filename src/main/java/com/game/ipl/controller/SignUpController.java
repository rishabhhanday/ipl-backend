package com.game.ipl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.model.UserCreationRequest;
import com.game.ipl.model.UserCreationResponse;
import com.game.ipl.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@Validated
@Slf4j
public class SignUpController {
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserCreationResponse> createUser(@Valid @RequestParam MultipartFile file, @RequestParam String userInfo) throws IOException {
        UserCreationRequest userCreationRequest = objectMapper.readValue(userInfo, UserCreationRequest.class);

        userCreationRequest.validateUserCreationRequest();
        userCreationRequest.setUserImage(file);
        log.info("STARTED SIGNUP | firstName={}, lastName={}", userCreationRequest.getFirstName(), userCreationRequest.getLastName());
        UserCreationResponse userCreationResponse = userService.save(userCreationRequest);

        log.info("STARTED SIGNUP | firstName={}, lastName={}", userCreationRequest.getFirstName(), userCreationRequest.getLastName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userCreationResponse);
    }
}
