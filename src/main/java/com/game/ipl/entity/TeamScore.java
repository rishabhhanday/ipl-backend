package com.game.ipl.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.game.ipl.entity.converters.UniqueListByMatchIDConverter;
import com.game.ipl.entity.converters.UniqueListStringConverter;
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
    @DynamoDBTypeConverted(converter = UniqueListStringConverter.class)
    private List<String> winningMatchIds = new ArrayList<>();
    @DynamoDBTypeConverted(converter = UniqueListByMatchIDConverter.class)
    private List<LosingMatchInfo> losingMatchInfos = new ArrayList<>();
}
