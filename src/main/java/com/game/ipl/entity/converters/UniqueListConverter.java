package com.game.ipl.entity.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.util.List;
import java.util.stream.Collectors;

public class UniqueListConverter implements DynamoDBTypeConverter<List<String>, List<String>> {
    @Override
    public List<String> convert(List<String> strings) {
        return strings.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> unconvert(List<String> strings) {
        return strings.stream().distinct().collect(Collectors.toList());
    }
}
