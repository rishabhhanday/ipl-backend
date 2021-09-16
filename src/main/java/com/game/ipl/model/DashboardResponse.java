package com.game.ipl.model;

import com.game.ipl.entity.TeamScore;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DashboardResponse {
    private Map<String, TeamScore> teamScore;
    private Integer points = 0;
    private UserInfoResponse userInfo;
    private Integer rank;
    private Integer winningStreak = 0;
    private Integer longestWinningStreak = 0;
}
