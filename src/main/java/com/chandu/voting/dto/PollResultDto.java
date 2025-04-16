package com.chandu.voting.dto;

import lombok.*;

import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PollResultDto {
    private UUID participantId;
    String participantName;
    private Long noOfVotes;
}
