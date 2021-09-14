package com.game.ipl.exceptions.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.game.ipl.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, String>> unhandledException(Exception ex) {
        log.error(ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());

        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(value = MatchResultPopulationFailed.class)
    public ResponseEntity<List<DynamoDBMapper.FailedBatch>> failureInResultPopulation(MatchResultPopulationFailed matchResultPopulationFailed) {
        log.info(matchResultPopulationFailed.getFailedBatches().toString());
        return ResponseEntity.status(500).body(matchResultPopulationFailed.getFailedBatches());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> failureInUserCreation(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getFieldError().getField() + " " + ex.getFieldError().getDefaultMessage());
        return ResponseEntity.status(400).body(response);
    }


    @ExceptionHandler(value = {JsonProcessingException.class, FailedCreateUserException.class, LoginFailedException.class, VotingFailedException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        log.error(ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(value = TokenValidationException.class)
    public ResponseEntity<Map<String, String>> failureInTokenValidation(TokenValidationException ex) {
        log.error(ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());

        return ResponseEntity.status(403).body(response);
    }

}
