package com.game.ipl.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.game.ipl.entity.converters.UniqueListConverter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamoDBDocument
public class TeamScore {
    @DynamoDBTypeConverted(converter = UniqueListConverter.class)
    private List<String> winningMatchIds = new ArrayList<>();
    @DynamoDBTypeConverted(converter = UniqueListConverter.class)
    private List<String> losingMatchIds = new ArrayList<>();
}
