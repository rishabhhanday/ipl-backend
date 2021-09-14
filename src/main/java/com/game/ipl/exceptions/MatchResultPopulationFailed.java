package com.game.ipl.exceptions;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.Getter;

import java.util.List;

@Getter
public class MatchResultPopulationFailed extends RuntimeException {
    private List<DynamoDBMapper.FailedBatch> failedBatches;

    public MatchResultPopulationFailed(List<DynamoDBMapper.FailedBatch> failedBatches) {
        super(failedBatches.get(0).getException());
        this.failedBatches = failedBatches;
    }
}
