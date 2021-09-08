package com.game.ipl.model;


import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserMatchInfoResponse {
    private String matchId;
    private Date matchOn;
    private String teamA;
    private String teamB;
    private String winner;
    private String stadium;
    private Integer matchPoint;
    private Integer voteRemaining = 2;
    private String votedOn = "SKIPPED";
}
