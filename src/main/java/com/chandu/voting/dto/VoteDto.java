package com.chandu.voting.dto;


import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VoteDto {
    private UUID id;
    private UUID userId;
    private UUID participantId;
    private UUID pollId;
}
