package com.game.ipl.exceptions;

public class InvalidMatchHistory extends RuntimeException {
    public InvalidMatchHistory(String reason) {
        super(reason);
    }
}
