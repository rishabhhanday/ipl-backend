package com.game.ipl.model;

import com.game.ipl.entity.UserResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ScoreboardResponse {
    private Integer totalScore;
    private List<DashboardResponse> participants;
}
