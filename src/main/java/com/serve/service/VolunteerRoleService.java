package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.Volunteer;
import com.serve.domain.VolunteerRole;
import com.serve.repository.EventRepository;
import com.serve.repository.VolunteerRepository;
import com.serve.repository.VolunteerRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VolunteerRoleService {

    private final EventRepository eventRepository;
    private final VolunteerRoleRepository volunteerRoleRepository;
    private final VolunteerRepository volunteerRepository;

    public VolunteerRoleService(
            EventRepository eventRepository,
            VolunteerRoleRepository volunteerRoleRepository,
            VolunteerRepository volunteerRepository
    ) {
        this.eventRepository = eventRepository;
        this.volunteerRoleRepository = volunteerRoleRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public VolunteerRole createVolunteerRole(UUID eventId, VolunteerRole role) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        role.setEvent(event);
        if (role.getRequiredPeople() == null) {
            role.setRequiredPeople(1);
        }

        return volunteerRoleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public List<VolunteerRole> getRolesByEvent(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }

        return volunteerRoleRepository.findByEventId(eventId);
    }

    public VolunteerRole assignVolunteer(UUID roleId, UUID volunteerId) {
        VolunteerRole role = volunteerRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new EntityNotFoundException("Volunteer not found"));

        boolean alreadyAssigned = role.getVolunteers().stream()
                .anyMatch(assignedVolunteer -> assignedVolunteer.getId().equals(volunteerId));

        if (!alreadyAssigned) {
            role.getVolunteers().add(volunteer);
        }

        return volunteerRoleRepository.save(role);
    }

    public VolunteerRole unassignVolunteer(UUID roleId, UUID volunteerId) {
        VolunteerRole role = volunteerRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new EntityNotFoundException("Volunteer not found"));

        role.getVolunteers().removeIf(assignedVolunteer -> assignedVolunteer.getId().equals(volunteer.getId()));

        return volunteerRoleRepository.save(role);
    }
}
