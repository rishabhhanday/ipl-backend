package com.game.ipl.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.game.ipl.entity.MatchInfo;
import com.game.ipl.entity.UserInfo;
import com.game.ipl.entity.VotingInfo;
import com.game.ipl.exceptions.InvalidMatchHistory;
import com.game.ipl.model.HistoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class HistoryService {
    @Autowired
    private DynamoDBMapper mapper;

    public HistoryResponse getMatchVotingHistory(String matchId) {
        Pair<Boolean, MatchInfo> matchInfo = isMatchIdValid(matchId);
        if (Boolean.FALSE.equals(matchInfo.getFirst())) {
            throw new InvalidMatchHistory("match not started " + matchId);
        }

        List<VotingInfo> votingInfos = getVotingInfoByMatchId(matchId);
        return getHistoryResponse(votingInfos, matchInfo.getSecond());
    }

    private HistoryResponse getHistoryResponse(List<VotingInfo> votingInfos, MatchInfo matchInfo) {
        List<String> winnerUsersList = new ArrayList<>();
        List<String> losingUsersList = new ArrayList<>();
        List<String> skippedUsersList = new ArrayList<>();

        List<UserInfo> userInfos = mapper.scan(UserInfo.class, new DynamoDBScanExpression());

        userInfos.forEach(userInfo -> {
            String username = userInfo.getUsername();
            String fullName = userInfo.getFirstName() + " " + userInfo.getLastName();
            Optional<VotingInfo> votingInformation = votingInfos
                    .stream()
                    .filter(votingInfo -> votingInfo.getUsername().equals(username))
                    .findFirst();

            if (votingInformation.isPresent()) {
                if (votingInformation.get().getVotedOn().equals(matchInfo.getWinner())) {
                    winnerUsersList.add(fullName);
                } else {
                    losingUsersList.add(fullName);
                }
            } else {
                skippedUsersList.add(fullName);
            }
        });

        return HistoryResponse
                .builder()
                .winnerUsersList(winnerUsersList)
                .losingUsersList(losingUsersList)
                .skippedUsersList(skippedUsersList)
                .matchInfo(matchInfo)
                .build();
    }

    private List<VotingInfo> getVotingInfoByMatchId(String matchId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(matchId));

        DynamoDBQueryExpression<VotingInfo> queryExpression = new DynamoDBQueryExpression<VotingInfo>()
                .withKeyConditionExpression("matchId = :val1")
                .withExpressionAttributeValues(eav);

        return mapper.query(VotingInfo.class, queryExpression);
    }

    private Pair<Boolean, MatchInfo> isMatchIdValid(String matchId) {
        MatchInfo matchInfo = mapper.load(MatchInfo.class, matchId);
        if (matchInfo.getMatchOn().toInstant().isAfter(Instant.now())) {
            return Pair.of(false, matchInfo);
        }

        return Pair.of(true, matchInfo);
    }
}
