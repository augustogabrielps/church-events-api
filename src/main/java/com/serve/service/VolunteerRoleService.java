package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.VolunteerRole;
import com.serve.repository.EventRepository;
import com.serve.repository.VolunteerRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class VolunteerRoleService {

    private final EventRepository eventRepository;
    private final VolunteerRoleRepository volunteerRoleRepository;

    public VolunteerRoleService(EventRepository eventRepository, VolunteerRoleRepository volunteerRoleRepository) {
        this.eventRepository = eventRepository;
        this.volunteerRoleRepository = volunteerRoleRepository;
    }

    public VolunteerRole createVolunteerRole(UUID eventId, VolunteerRole role) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        role.setEvent(event);

        return volunteerRoleRepository.save(role);
    }
}
