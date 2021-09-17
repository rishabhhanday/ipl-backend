package com.game.ipl.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.exceptions.FailedCreateUserException;
import com.game.ipl.exceptions.LoginFailedException;
import com.game.ipl.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private BasicPasswordEncryptor passwordEncryptor;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FileStorageService fileStorageService;

    public UserCreationResponse save(UserCreationRequest userCreationRequest) throws IOException {
        if (mapper.load(UserInfo.class, userCreationRequest.getUsername()) != null) {
            log.error("User id already present , create user with another id");
            throw new FailedCreateUserException("Username already present");
        }

        log.info("Saving image into s3 bucket , username={}", userCreationRequest.getUsername());
        String imageURL = "";
        if (userCreationRequest.getUserImage() != null) {
            imageURL = fileStorageService.saveUserImage(userCreationRequest.getUsername(), userCreationRequest.getUserImage());
        }

        UserInfo userInfo = objectMapper.convertValue(userCreationRequest, UserInfo.class);
        log.info("Saving userInfo to database , username: {}", userInfo.getUsername());

        userInfo.setPassword(passwordEncryptor.encryptPassword(userInfo.getPassword()));
        userInfo.setImageURL(imageURL);
        mapper.save(userInfo);
        log.info("User successfully saved to database");

        log.info("Creating jwt token for username : {}", userInfo.getUsername());
        String token = jwtService.createToken(userInfo);

        return UserCreationResponse.builder().token(token).build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        return Optional.ofNullable(mapper
                .load(UserInfo.class, loginRequest.getUsername()))
                .map(userInfo -> {
                    if (passwordEncryptor.checkPassword(loginRequest.getPassword(), userInfo.getPassword())) {
                        UserInfoResponse userInfoResponse = objectMapper.convertValue(userInfo, UserInfoResponse.class);

                        return LoginResponse
                                .builder()
                                .token(jwtService.createToken(userInfo))
                                .userInfo(userInfoResponse)
                                .build();
                    }

                    throw new LoginFailedException("password did not match.");
                })
                .orElseThrow(() -> new LoginFailedException("username is invalid"));
    }
}
