package com.game.ipl.exceptions;

public class FailedCreateUserException extends RuntimeException {
    public FailedCreateUserException(String reason) {
        super(reason);
    }
}
