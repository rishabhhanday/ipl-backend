package com.game.ipl.exceptions;

public class TokenValidationException extends RuntimeException {
    public TokenValidationException(String reason) {
        super(reason);
    }

    public TokenValidationException(String reason , Exception ex){
        super(reason,ex);
    }
}
