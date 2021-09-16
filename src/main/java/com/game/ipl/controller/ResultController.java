package com.game.ipl.controller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.game.ipl.entity.*;
import com.game.ipl.exceptions.MatchResultPopulationFailed;
import com.game.ipl.services.JWTService;
import com.game.ipl.services.VotingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
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
    /*@PostMapping("/result")
    public ResponseEntity<List<UserResult>> populateResult(@RequestParam String winner, @RequestParam String matchId) {
        MatchInfo matchInfo = this.updateMatchWinner(winner, matchId);
        return ResponseEntity.ok(this.populateUsers(winner, matchId, matchInfo));
    }*/


    private MatchInfo updateMatchWinner(String winner, String matchId) {
        MatchInfo matchInfo = mapper.load(MatchInfo.class, matchId);
        matchInfo.setWinner(winner);
        mapper.save(matchInfo);
        log.info("winner updated, {}", matchInfo);
        return matchInfo;
    }

    private void populatePointsWhenMatchLost(UserResult userResult, Integer matchPoints) {
        userResult.setWinningStreak(0);
        userResult.setPoints(getParsedPoints(userResult.getPoints()) - matchPoints);
    }

    private void populatePointsWhenMatchWon(UserResult userResult, Integer matchPoints) {
        Integer winnerStreak = userResult.getWinningStreak();
        if (winnerStreak == null || winnerStreak == 0) {
            winnerStreak = 1;
        } else {
            winnerStreak = winnerStreak + 1;
        }

        if (winnerStreak >= userResult.getLongestWinningStreak()) {
            userResult.setLongestWinningStreak(winnerStreak);
        }
        userResult.setWinningStreak(winnerStreak);

        userResult.setPoints(getParsedPoints(userResult.getPoints()) + matchPoints);
    }

    private void populatePointsWhenMatchSkipped(UserResult userResult, Integer matchPoints) {
        userResult.setWinningStreak(0);
        userResult.setPoints(getParsedPoints(userResult.getPoints()) - (2 * matchPoints));
    }

    private Integer getParsedPoints(Integer points) {
        return points == null ? 0 : points;
    }

    private UserResult populateUser(String winner, String matchId, UserInfo userInfo, MatchInfo matchInfo) {

        return votingService.getVotingInfo(matchId, userInfo.getUsername()).map(votingInfo -> {

            // voting is done by some user
            UserResult userResult = mapper.load(UserResult.class, userInfo.getUsername());

            if (userResult == null) {
                // if voting was done , but result table was not populated .
                Map<String, TeamScore> teamScoreMap = new HashMap<>();
                TeamScore teamScore = votingInfo.getVotedOn().equals(winner) ? createWinningTeamScore(matchId) : createLosingTeamScore(matchId, false);
                teamScoreMap.put(votingInfo.getVotedOn(), teamScore);
                userResult = UserResult.builder().longestWinningStreak(0).winningStreak(0).userInfo(userInfo).username(userInfo.getUsername()).teamScore(teamScoreMap).points(0).build();
                if (votingInfo.getVotedOn().equals(winner)) {
                    this.populatePointsWhenMatchWon(userResult, votingInfo.getMatchInfo().getMatchPoint());
                } else {
                    this.populatePointsWhenMatchLost(userResult, votingInfo.getMatchInfo().getMatchPoint());
                }
            } else if (userResult.getTeamScore().get(votingInfo.getVotedOn()) == null) {
                // if result table was present but first time vote on team .
                TeamScore teamScore = votingInfo.getVotedOn().equals(winner) ? createWinningTeamScore(matchId) : createLosingTeamScore(matchId, false);
                if (votingInfo.getVotedOn().equals(winner)) {
                    this.populatePointsWhenMatchWon(userResult, votingInfo.getMatchInfo().getMatchPoint());
                } else {
                    this.populatePointsWhenMatchLost(userResult, votingInfo.getMatchInfo().getMatchPoint());
                }
                userResult.getTeamScore().put(votingInfo.getVotedOn(), teamScore);
            } else {
                // if result table was present and voted on team more than once
                // tested by rishabh
                TeamScore teamScore = userResult.getTeamScore().get(votingInfo.getVotedOn());
                if (votingInfo.getVotedOn().equals(winner) && !teamScore.getWinningMatchIds().contains(matchId)) {
                    this.populatePointsWhenMatchWon(userResult, votingInfo.getMatchInfo().getMatchPoint());
                } else if (!isMatchIDPresent(teamScore.getLosingMatchInfos(), matchId)) {
                    this.populatePointsWhenMatchLost(userResult, votingInfo.getMatchInfo().getMatchPoint());
                }
                Boolean winnerOrLoser = votingInfo.getVotedOn().equals(winner) ? teamScore.getWinningMatchIds().add(matchId) : teamScore.getLosingMatchInfos().add(LosingMatchInfo.builder().isVotingSkipped(false).matchId(matchId).build());

            }

            return userResult;
        }).orElseGet(() -> {
            UserResult userResult = mapper.load(UserResult.class, userInfo.getUsername());

            // voting was skipped
            if (userResult == null) {

                // user result not found
                Map<String, TeamScore> teamScoreMap = new HashMap<>();
                TeamScore teamScore = createLosingTeamScore(matchId, true);
                teamScoreMap.put(winner, teamScore);
                userResult = UserResult.builder().longestWinningStreak(0).winningStreak(0).userInfo(userInfo).points(0).username(userInfo.getUsername()).teamScore(teamScoreMap).build();
                this.populatePointsWhenMatchSkipped(userResult, matchInfo.getMatchPoint());
            } else if (userResult.getTeamScore().get(winner) == null) {
                // user result found but data of losing team missing.
                this.populatePointsWhenMatchSkipped(userResult, matchInfo.getMatchPoint());
                TeamScore teamScore = createLosingTeamScore(matchId, true);
                userResult.getTeamScore().put(winner, teamScore);
            } else {
                //use result found and data of losing team is present.
                TeamScore teamScore = userResult.getTeamScore().get(winner);
                if (!isMatchIDPresent(teamScore.getLosingMatchInfos(), matchId)) {
                    this.populatePointsWhenMatchSkipped(userResult, matchInfo.getMatchPoint());
                }
                teamScore.getLosingMatchInfos().add(LosingMatchInfo.builder().isVotingSkipped(true).matchId(matchId).build());
            }

            return userResult;
        });
    }

    private TeamScore createWinningTeamScore(String matchId) {
        TeamScore teamScore = new TeamScore();
        List<String> winningMatchIds = new ArrayList<>();
        winningMatchIds.add(matchId);
        teamScore.setWinningMatchIds(winningMatchIds);
        return teamScore;
    }

    private TeamScore createLosingTeamScore(String matchId, boolean isVotingSkipped) {
        TeamScore teamScore = new TeamScore();
        List<LosingMatchInfo> losingMatchInfos = new ArrayList<>();
        losingMatchInfos.add(LosingMatchInfo.builder().isVotingSkipped(isVotingSkipped).matchId(matchId).build());
        teamScore.setLosingMatchInfos(losingMatchInfos);
        return teamScore;
    }


    private List<UserResult> populateUsers(String winner, String matchId, MatchInfo matchInfo) {
        List<UserInfo> userInfos = mapper.scan(UserInfo.class, new DynamoDBScanExpression());
        List<UserResult> userResults = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            userResults.add(populateUser(winner, matchId, userInfo, matchInfo));
        }

        log.info("Calculating rank");
        userResults.sort((userOne, userTwo) -> userTwo.getPoints() - userOne.getPoints());

        Integer rank = 1;
        for (UserResult userResult : userResults) {
            userResult.setRank(rank++);
        }

        final List<DynamoDBMapper.FailedBatch> failedBatches = mapper.batchSave(userResults);
        if (!failedBatches.isEmpty()) {
            throw new MatchResultPopulationFailed(failedBatches);
        }

        return userResults;
    }

    private boolean isMatchIDPresent(List<LosingMatchInfo> losingMatchInfos, String matchId) {
        for (LosingMatchInfo losingMatchInfo : losingMatchInfos) {
            if (losingMatchInfo.getMatchId().equals(matchId)) {
                return true;
            }
        }

        return false;
    }
}
