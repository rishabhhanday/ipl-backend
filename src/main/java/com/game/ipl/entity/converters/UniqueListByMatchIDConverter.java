package com.game.ipl.entity.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.game.ipl.entity.LosingMatchInfo;

import java.util.ArrayList;
import java.util.List;

public class UniqueListByMatchIDConverter implements DynamoDBTypeConverter<List<LosingMatchInfo>, List<LosingMatchInfo>> {
    @Override
    public List<LosingMatchInfo> convert(List<LosingMatchInfo> losingMatchInfos) {
        List<LosingMatchInfo> uniqueLosingMatchInfos = new ArrayList<>();
        for (int i = 0; i < losingMatchInfos.size(); i++) {
            LosingMatchInfo losingMatchInfo = losingMatchInfos.get(i);
            boolean isUnique = true;

            inner:
            for (int j = i + 1; j < losingMatchInfos.size(); j++) {
                if (losingMatchInfo.getMatchId().equals(losingMatchInfos.get(j).getMatchId())) {
                    isUnique = false;
                    break inner;
                }
            }

            if(isUnique) uniqueLosingMatchInfos.add(losingMatchInfo);
        }

        return uniqueLosingMatchInfos;
    }

    @Override
    public List<LosingMatchInfo> unconvert(List<LosingMatchInfo> losingMatchInfos) {
        List<LosingMatchInfo> uniqueLosingMatchInfos = new ArrayList<>();
        for (int i = 0; i < losingMatchInfos.size(); i++) {
            LosingMatchInfo losingMatchInfo = losingMatchInfos.get(i);
            boolean isUnique = true;

            inner:
            for (int j = i + 1; j < losingMatchInfos.size(); j++) {
                if (losingMatchInfo.getMatchId().equals(losingMatchInfos.get(j).getMatchId())) {
                    isUnique = false;
                    break inner;
                }
            }

            if(isUnique) uniqueLosingMatchInfos.add(losingMatchInfo);
        }

        return uniqueLosingMatchInfos;
    }
}
