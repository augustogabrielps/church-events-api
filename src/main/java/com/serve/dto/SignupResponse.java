package com.serve.dto;

import com.serve.domain.SignupStatus;
import com.serve.domain.VolunteerSignup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SignupResponse(
        UUID id,
        UUID eventId,
        UUID userId,
        UUID roleId,
        String roleName,
        UUID eventDateId,
        LocalDate eventDate,
        SignupStatus status,
        int remainingSlots,
        LocalDateTime createdAt
) {

    public static SignupResponse from(VolunteerSignup signup, int remainingSlots) {
        return new SignupResponse(
                signup.getId(),
                signup.getRole().getEvent().getId(),
                signup.getUser().getId(),
                signup.getRole().getId(),
                signup.getRole().getName(),
                signup.getEventDate().getId(),
                signup.getEventDate().getEventDate(),
                signup.getStatus(),
                remainingSlots,
                signup.getCreatedAt()
        );
    }
}
