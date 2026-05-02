package com.serve.dto;

import com.serve.domain.Volunteer;

import java.util.UUID;

public record VolunteerResponse(
        UUID id,
        String name,
        String email
) {

    public static VolunteerResponse from(Volunteer volunteer) {
        if (volunteer == null) {
            return null;
        }

        return new VolunteerResponse(
                volunteer.getId(),
                volunteer.getName(),
                volunteer.getEmail()
        );
    }
}
