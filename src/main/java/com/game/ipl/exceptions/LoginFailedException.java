package com.game.ipl.exceptions;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String reason) {
        super(reason);
    }
}
