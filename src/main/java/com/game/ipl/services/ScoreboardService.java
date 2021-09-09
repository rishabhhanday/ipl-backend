package com.game.ipl.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.entity.UserResult;
import com.game.ipl.model.DashboardResponse;
import com.game.ipl.model.ScoreboardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScoreboardService {
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;

    public ScoreboardResponse getScore() {
        List<UserResult> userResultList = new ArrayList<>(mapper.scan(UserResult.class, new DynamoDBScanExpression()));
        if (userResultList.isEmpty()) {
            List<UserInfo> userInfos = mapper.scan(UserInfo.class, new DynamoDBScanExpression());
            userResultList = new ArrayList<>();

            for (UserInfo userInfo : userInfos) {
                UserResult userResult = new UserResult();
                userResult.setPoints(0);
                userResult.setUserInfo(userInfo);
                userResult.setUsername(userInfo.getUsername());

                userResultList.add(userResult);
            }
        } else {
            userResultList.sort((firstUser, secondUser) -> getPoints(secondUser.getPoints()) - getPoints(firstUser.getPoints()));
        }

        Integer totalScore = calulateTotoalScore(userResultList);
        List<DashboardResponse> dashboardResponses = userResultList.stream().map(userResult -> objectMapper.convertValue(userResult, DashboardResponse.class)).collect(Collectors.toList());

        log.info("Top scorer is : {}", dashboardResponses.get(0));
        return ScoreboardResponse
                .builder()
                .participants(dashboardResponses)
                .totalScore(totalScore)
                .build();
    }

    private Integer calulateTotoalScore(List<UserResult> userResultList) {
        int totalScore = 0;
        for (UserResult userResult : userResultList) {
            totalScore = totalScore + getPoints(userResult.getPoints());
        }

        return totalScore;
    }

    Integer getPoints(Integer points) {
        return points == null ? 0 : points;
    }
}
