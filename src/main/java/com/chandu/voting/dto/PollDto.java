package com.chandu.voting.dto;


import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PollDto {
    private UUID id;
    private String title;
    private List<ParticipantDto> participants;
}
