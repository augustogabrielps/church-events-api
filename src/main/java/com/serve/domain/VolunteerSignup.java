package com.serve.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "volunteer_signups")
@Getter
@Setter
public class VolunteerSignup {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private VolunteerRole role;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_date_id", nullable = false)
    private EventDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignupStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
