package com.serve.dto;

import com.serve.domain.VolunteerRole;

import java.util.UUID;

public record VolunteerRoleResponse(
        UUID id,
        UUID eventId,
        String name,
        String description,
        Integer requiredPeople
) {

    public static VolunteerRoleResponse from(VolunteerRole role) {
        return new VolunteerRoleResponse(
                role.getId(),
                role.getEvent().getId(),
                role.getName(),
                role.getDescription(),
                role.getRequiredPeople()
        );
    }
}
