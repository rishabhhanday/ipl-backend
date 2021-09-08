package com.game.ipl.exceptions;

public class VotingFailedException extends RuntimeException {
    public VotingFailedException(String reason) {
        super(reason);
    }
}
