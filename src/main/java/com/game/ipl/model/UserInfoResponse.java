package com.game.ipl.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserInfoResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String emailId;
    private String department;
    private String role;
}
