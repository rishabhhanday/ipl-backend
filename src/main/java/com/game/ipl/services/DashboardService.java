package com.game.ipl.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.entity.TeamScore;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.entity.UserResult;
import com.game.ipl.model.DashboardResponse;
import com.game.ipl.model.UserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${ipl.teams}")
    private List<String> teams;

    public DashboardResponse getDashboardResponse(String username) {
        UserResult userResult = mapper.load(UserResult.class, username);

        if (userResult == null) {
            DashboardResponse dashboardResponse = new DashboardResponse();
            dashboardResponse.setPoints(0);
            dashboardResponse.setUserInfo(objectMapper.convertValue(mapper.load(UserInfo.class, username), UserInfoResponse.class));
            Map<String, TeamScore> teamScoreMap = new HashMap<>();
            teams.forEach(team -> {
                teamScoreMap.put(team, new TeamScore());
            });
            dashboardResponse.setTeamScore(teamScoreMap);
            dashboardResponse.setPoints(0);
            return dashboardResponse;
        } else {
            DashboardResponse dashboardResponse = objectMapper.convertValue(userResult, DashboardResponse.class);
            Map<String, TeamScore> teamScoreMap = dashboardResponse.getTeamScore();
            teams.forEach(team -> {
                if (!teamScoreMap.containsKey(team)) {
                    teamScoreMap.put(team, new TeamScore());
                }
            });
            return dashboardResponse;
        }
    }

}
