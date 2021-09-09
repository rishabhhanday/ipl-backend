package com.game.ipl.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.game.ipl.entity.MatchInfo;
import com.game.ipl.entity.TeamScore;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.entity.UserResult;
import com.game.ipl.services.JWTService;
import com.game.ipl.services.VotingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class ResultController {
    @Autowired
    private DynamoDBMapper mapper;
    @Autowired
    private VotingService votingService;
    @Autowired
    private JWTService jwtService;

    //disable for prod
    @PostMapping("/result")
    public ResponseEntity<List<UserResult>> populateResult(@RequestParam String winner, @RequestParam String matchId) {
        this.updateMatchWinner(winner, matchId);
        return ResponseEntity.ok(this.populateUsers(winner, matchId));
    }


    private void updateMatchWinner(String winner, String matchId) {
        MatchInfo matchInfo = mapper.load(MatchInfo.class, matchId);
        matchInfo.setWinner(winner);
        mapper.save(matchInfo);
        log.info("winner updated, {}", matchInfo);
    }

    private UserResult populateUser(String winner, String matchId, UserInfo userInfo) {
        UserResult result = votingService.getVotingInfo(matchId, userInfo.getUsername()).map(votingInfo -> {

            // voting is done by some user
            UserResult userResult = mapper.load(UserResult.class, userInfo.getUsername());

            if (userResult == null) {
                // if voting was done , but result table was not populated .
                Map<String, TeamScore> teamScoreMap = new HashMap<>();
                TeamScore teamScore = votingInfo.getVotedOn().equals(winner) ? createWinningTeamScore(matchId) : createLosingTeamScore(matchId);
                teamScoreMap.put(votingInfo.getVotedOn(), teamScore);
                userResult = UserResult.builder().userInfo(userInfo).username(userInfo.getUsername()).teamScore(teamScoreMap).build();
                if (!votingInfo.getVotedOn().equals(winner)) {
                    userResult.setPoints(votingInfo.getMatchInfo().getMatchPoint());
                }
            } else if (userResult.getTeamScore().get(votingInfo.getVotedOn()) == null) {
                // if result table was present but first time vote on team .
                TeamScore teamScore = votingInfo.getVotedOn().equals(winner) ? createWinningTeamScore(matchId) : createLosingTeamScore(matchId);
                if (!votingInfo.getVotedOn().equals(winner)) {
                    userResult.setPoints(userResult.getPoints() == null ? 0 : userResult.getPoints() + votingInfo.getMatchInfo().getMatchPoint());
                }
                userResult.getTeamScore().put(votingInfo.getVotedOn(), teamScore);
            } else {
                // if result table was present and voted on team more than once
                // tested by rishabh
                TeamScore teamScore = userResult.getTeamScore().get(votingInfo.getVotedOn());
                if (!votingInfo.getVotedOn().equals(winner) && !teamScore.getLosingMatchIds().contains(matchId)) {
                    userResult.setPoints(userResult.getPoints() == null ? 0 : userResult.getPoints() + votingInfo.getMatchInfo().getMatchPoint());
                }
                Boolean winnerOrLoser = votingInfo.getVotedOn().equals(winner) ? teamScore.getWinningMatchIds().add(matchId) : teamScore.getLosingMatchIds().add(matchId);

            }

            return userResult;
        }).orElseGet(() -> {
            UserResult userResult = mapper.load(UserResult.class, userInfo.getUsername());

            // voting was skipped
            if (userResult == null) {

                // user result not found
                Map<String, TeamScore> teamScoreMap = new HashMap<>();
                TeamScore teamScore = createLosingTeamScore(matchId);
                teamScoreMap.put(winner, teamScore);
                userResult = UserResult.builder().userInfo(userInfo).points(mapper.load(MatchInfo.class, matchId).getMatchPoint()).username(userInfo.getUsername()).teamScore(teamScoreMap).build();
            } else if (userResult.getTeamScore().get(winner) == null) {
                // user result found but data of losing team missing.

                userResult.setPoints(userResult.getPoints() == null ? 0 : userResult.getPoints() + mapper.load(MatchInfo.class, matchId).getMatchPoint());
                TeamScore teamScore = createLosingTeamScore(matchId);
                userResult.getTeamScore().put(winner, teamScore);
            } else {
                //use result found and data of losing team is present.
                TeamScore teamScore = userResult.getTeamScore().get(winner);
                if (!teamScore.getLosingMatchIds().contains(matchId)) {
                    userResult.setPoints(userResult.getPoints() == null ? 0 : userResult.getPoints() + mapper.load(MatchInfo.class, matchId).getMatchPoint());
                }
                teamScore.getLosingMatchIds().add(matchId);
            }

            return userResult;
        });

        mapper.save(result);
        log.info("result {}", result);

        return result;
    }

    private TeamScore createWinningTeamScore(String winner) {
        TeamScore teamScore = new TeamScore();
        List<String> winningMatchIds = new ArrayList<>();
        winningMatchIds.add(winner);
        teamScore.setWinningMatchIds(winningMatchIds);
        return teamScore;
    }

    private TeamScore createLosingTeamScore(String winner) {
        TeamScore teamScore = new TeamScore();
        List<String> losingMatchIds = new ArrayList<>();
        losingMatchIds.add(winner);
        teamScore.setLosingMatchIds(losingMatchIds);
        return teamScore;
    }


    private List<UserResult> populateUsers(String winner, String matchId) {
        List<UserInfo> userInfos = mapper.scan(UserInfo.class, new DynamoDBScanExpression());
        List<UserResult> userResults = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            userResults.add(populateUser(winner, matchId, userInfo));
        }

        return userResults;
    }
}
