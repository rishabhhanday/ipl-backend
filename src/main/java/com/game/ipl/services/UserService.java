package com.game.ipl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.exceptions.FailedCreateUserException;
import com.game.ipl.exceptions.LoginFailedException;
import com.game.ipl.model.LoginRequest;
import com.game.ipl.model.LoginResponse;
import com.game.ipl.model.UserCreationRequest;
import com.game.ipl.model.UserCreationResponse;
import com.game.ipl.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private BasicPasswordEncryptor passwordEncryptor;

    public UserCreationResponse save(UserCreationRequest userCreationRequest) {
        if (userRepository.findById(userCreationRequest.getUsername()).isPresent()) {
            log.error("User id already present , create user with another id");
            throw new FailedCreateUserException("Username already present");
        }

        UserInfo userInfo = new ObjectMapper().convertValue(userCreationRequest, UserInfo.class);
        log.info("Saving userInfo to database , username: {}", userInfo.getUsername());

        userInfo.setPassword(passwordEncryptor.encryptPassword(userInfo.getPassword()));
        userRepository.save(userInfo);
        log.info("User successfully saved to database");

        log.info("Creating jwt token for username : {}", userInfo.getUsername());
        String token = jwtService.createToken(userInfo);

        return UserCreationResponse.builder().token(token).build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        return userRepository
                .findById(loginRequest.getUsername())
                .map(userInfo -> {
                    if (passwordEncryptor.checkPassword(loginRequest.getPassword(), userInfo.getPassword())) {
                        return LoginResponse.builder().token(jwtService.createToken(userInfo)).build();
                    }

                    throw new LoginFailedException("password did not match.");
                })
                .orElseThrow(() -> new LoginFailedException("username is invalid"));
    }
}
