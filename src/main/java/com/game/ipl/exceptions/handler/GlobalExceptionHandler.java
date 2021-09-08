package com.game.ipl.exceptions.handler;

import com.game.ipl.exceptions.FailedCreateUserException;
import com.game.ipl.exceptions.LoginFailedException;
import com.game.ipl.exceptions.TokenValidationException;
import com.game.ipl.exceptions.VotingFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, String>> unhandledException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());

        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> failureInUserCreation(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getFieldError().getField() + " " + ex.getFieldError().getDefaultMessage());
        return ResponseEntity.status(400).body(response);
    }


    @ExceptionHandler(value = {FailedCreateUserException.class, LoginFailedException.class, VotingFailedException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());

        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(value = TokenValidationException.class)
    public ResponseEntity<Map<String, String>> failureInTokenValidation(TokenValidationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", ex.getMessage());

        return ResponseEntity.status(403).body(response);
    }

}
