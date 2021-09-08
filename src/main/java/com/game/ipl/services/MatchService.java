package com.game.ipl.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.ipl.entity.MatchInfo;
import com.game.ipl.model.UserMatchInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.time.temporal.ChronoUnit.HOURS;

@Service
@Slf4j
public class MatchService {
    @Autowired
    VotingService votingService;
    @Autowired
    private DynamoDBMapper mapper;

    public MatchInfo save(MatchInfo matchInfo) {
        mapper.save(matchInfo);
        log.info("matchInfo succesfully saved : {}", matchInfo);
        return matchInfo;
    }

    public List<MatchInfo> getNextMatches(Integer limit) {
        List<MatchInfo> matchInfos = new ArrayList<>(mapper.scan(MatchInfo.class, new DynamoDBScanExpression()));
        matchInfos.sort((matchA, matchB) -> {
            if (matchA.getMatchOn().toInstant().isBefore(matchB.getMatchOn().toInstant())) {
                return -1;
            } else if (matchA.getMatchOn().toInstant().isAfter(matchB.getMatchOn().toInstant())) return 1;

            return 0;
        });

        return matchInfos
                .stream()
                .filter(matchInfo -> matchInfo.getMatchOn().toInstant().isAfter(Instant.now().plus(1, HOURS)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<UserMatchInfoResponse> getNextMatchesByUsername(Integer limit, String username) {
        List<MatchInfo> matchInfos = getNextMatches(limit);

        log.info("Got matchInfo from database, {}", matchInfos);
        List<UserMatchInfoResponse> userMatchInfoResponses = new ArrayList<>();
        for (MatchInfo matchInfo : matchInfos) {
            userMatchInfoResponses.add(getVotingInfo(matchInfo, username));
        }

        return userMatchInfoResponses;
    }

    private UserMatchInfoResponse getVotingInfo(MatchInfo matchInfo, String username) {
        UserMatchInfoResponse userMatchInfoResponse = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .convertValue(matchInfo, UserMatchInfoResponse.class);
        userMatchInfoResponse.setMatchId(matchInfo.getId());

        return votingService.getVotingInfo(matchInfo.getId(), username)
                .map(votingInfo -> {
                    userMatchInfoResponse.setVotedOn(votingInfo.getVotedOn());
                    userMatchInfoResponse.setVoteRemaining(votingInfo.getVoteRemaining());

                    log.info("Voting information found in DB , {}", votingInfo);
                    return userMatchInfoResponse;
                }).orElse(userMatchInfoResponse);
    }
}
