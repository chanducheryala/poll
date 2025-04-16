package com.chandu.voting.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "voting")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    @JsonBackReference
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    @JsonBackReference
    private Participant participant;

}
