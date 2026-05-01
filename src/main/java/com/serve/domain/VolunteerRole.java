package com.serve.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "volunteer_roles")
public class VolunteerRole {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "required_people", nullable = false)
    private Integer requiredPeople;

    // Getters and Setters
}
