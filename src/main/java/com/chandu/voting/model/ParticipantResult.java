package com.chandu.voting.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "participant_result",
        indexes = {
                @Index(name = "idx_poll_result_poll_participant", columnList = "poll_id, participant_id")
        }
)
public class ParticipantResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    @JsonBackReference
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    @JsonBackReference
    private Participant participant;

    @Column(name = "votes")
    private BigInteger votes;

    @Column(name = "percentage")
    private Double percentage;
}
