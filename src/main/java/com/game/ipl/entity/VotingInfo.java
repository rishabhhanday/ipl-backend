package com.game.ipl.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

@DynamoDBTable(tableName = "votingInfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VotingInfo {
    @DynamoDBHashKey
    private String matchId;
    @DynamoDBRangeKey
    private String username;
    private Integer voteRemaining = 2;
    private String votedOn = "SKIPPED";
    private MatchInfo matchInfo;
}
