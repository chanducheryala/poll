package com.chandu.voting.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "poll")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Participant> participants;

    @OneToMany(mappedBy = "poll")
    @JsonManagedReference
    private List<Vote> votings;


    @OneToMany(mappedBy = "poll")
    @JsonManagedReference
    private List<ParticipantResult> participantResults;
}
