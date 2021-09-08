package com.game.ipl.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VotingRequest {
    private String matchId;
    private String voteOn;
}
