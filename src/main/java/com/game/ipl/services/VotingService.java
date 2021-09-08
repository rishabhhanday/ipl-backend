package com.game.ipl.services;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.game.ipl.entity.MatchInfo;
import com.game.ipl.entity.VotingInfo;
import com.game.ipl.exceptions.VotingFailedException;
import com.game.ipl.model.VotingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
@Slf4j
public class VotingService {
    @Autowired
    private DynamoDBMapper mapper;

    public VotingInfo vote(VotingRequest votingRequest, String username) {
        log.info("Getting matchInfo, {}", votingRequest);
        MatchInfo matchInfo = mapper.load(MatchInfo.class, votingRequest.getMatchId());
        if (matchInfo.getMatchOn().toInstant().isBefore(Instant.now().plus(1, HOURS))) {
            throw new VotingFailedException("Voting time expired");
        }

        VotingInfo votingInfo = this.getVotingInfo(votingRequest.getMatchId(), username).orElseGet(() -> VotingInfo.builder().matchInfo(matchInfo).matchId(votingRequest.getMatchId()).username(username).votedOn("SKIPPED").voteRemaining(2).build());
        log.info("Got vote details , {}", votingInfo);

        if (votingInfo.getVoteRemaining() == 0 || !(votingRequest.getVoteOn().equals(matchInfo.getTeamA()) || votingRequest.getVoteOn().equals(matchInfo.getTeamB()))) {
            throw new VotingFailedException("Number of votes remaining is 0 or incorrect voting team name.");
        }

        votingInfo.setVotedOn(votingRequest.getVoteOn());
        votingInfo.setVoteRemaining(votingInfo.getVoteRemaining() - 1);

        log.info("Saving vote , {} ", votingInfo);
        mapper.save(votingInfo);
        return votingInfo;
    }

    public Optional<VotingInfo> getVotingInfo(String matchId, String username) {
        log.info("Getting vote information from DB , matchId={} , username={}", matchId, username);

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(matchId));
        eav.put(":val2", new AttributeValue().withS(username));
        DynamoDBQueryExpression<VotingInfo> queryExpression = new DynamoDBQueryExpression<VotingInfo>()
                .withKeyConditionExpression("matchId = :val1 and username = :val2").withExpressionAttributeValues(eav);


        return mapper.query(VotingInfo.class, queryExpression)
                .stream()
                .findFirst();
    }
}
