package com.game.ipl.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import java.util.Date;

@DynamoDBTable(tableName = "matchInfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBDocument
public class MatchInfo {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;
    private Date matchOn;
    private String teamA;
    private String teamB;
    private String winner;
    private String stadium;
    private Integer matchPoint;
}
