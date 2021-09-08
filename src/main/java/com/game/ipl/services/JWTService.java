package com.game.ipl.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.exceptions.TokenValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class JWTService {
    private static final String USERNAME_CLAIM = "username";
    @Value("${jwt.hmac256.secret}")
    private String secret;

    public String createToken(UserInfo userInfo) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withIssuer("auth0")
                .withClaim(USERNAME_CLAIM, userInfo.getUsername())
                //.withExpiresAt(Date.from(Instant.now().plus(1, HOURS)))
                .sign(algorithm);

        return token;
    }

    public String getUsername(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build().verify(token.substring(7));

            return ofNullable(jwt.getClaim(USERNAME_CLAIM).asString())
                    .orElseThrow(() -> new TokenValidationException(USERNAME_CLAIM + " " + "missing"));
        } catch (JWTVerificationException | IndexOutOfBoundsException ex) {
            throw new TokenValidationException(ex.getMessage(), ex);
        }
    }
}
