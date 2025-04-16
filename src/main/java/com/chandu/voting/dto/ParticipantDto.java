package com.chandu.voting.dto;


import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantDto {
    private UUID id;
    private String name;
}
