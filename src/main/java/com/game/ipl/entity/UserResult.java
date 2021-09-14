package com.game.ipl.entity;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.Map;

@DynamoDBTable(tableName = "userResult")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserResult {
    @DynamoDBHashKey
    private String username;
    private Map<String, TeamScore> teamScore;
    private Integer points = 0;
    private Integer rank;
    private UserInfo userInfo;
}
