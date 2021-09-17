package com.game.ipl.model;

import com.game.ipl.entity.MatchInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class HistoryResponse {
    private MatchInfo matchInfo;
    private List<ParticipantVotingHistory> participantsVotingHistory;
}
