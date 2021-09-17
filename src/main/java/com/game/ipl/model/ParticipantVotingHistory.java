package com.game.ipl.model;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ParticipantVotingHistory {
    private String fullName;
    private String votedOn;
    private Status status;

    public enum Status {
        WON, LOST, NOT_VOTED, TBD
    }
}

