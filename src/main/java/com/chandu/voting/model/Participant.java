package com.chandu.voting.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "participant")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    @JsonBackReference
    private Poll poll;

    @OneToMany(mappedBy = "participant")
    List<Vote> votings;

}
