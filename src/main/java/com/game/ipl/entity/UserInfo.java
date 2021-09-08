package com.game.ipl.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@DynamoDBTable(tableName = "userInfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamoDBDocument
public class UserInfo {
    @DynamoDBHashKey
    private String username;
    private String firstName;
    private String lastName;
    private String emailId;
    private String department;
    private String role;
    @JsonIgnore
    private String password;
}

