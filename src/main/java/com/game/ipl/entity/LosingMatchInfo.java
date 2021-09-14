package com.game.ipl.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@DynamoDBDocument
public class LosingMatchInfo {
    private String matchId;
    private boolean isVotingSkipped;
}
