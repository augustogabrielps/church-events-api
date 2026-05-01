package com.serve.service;

import com.serve.domain.EventDate;
import com.serve.domain.SignupStatus;
import com.serve.domain.User;
import com.serve.domain.VolunteerRole;
import com.serve.domain.VolunteerSignup;
import com.serve.exception.BusinessRuleException;
import com.serve.exception.ResourceConflictException;
import com.serve.repository.EventDateRepository;
import com.serve.repository.EventRepository;
import com.serve.repository.UserRepository;
import com.serve.repository.VolunteerRoleRepository;
import com.serve.repository.VolunteerSignupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VolunteerSignupService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final VolunteerRoleRepository roleRepository;
    private final EventDateRepository eventDateRepository;
    private final VolunteerSignupRepository signupRepository;

    public VolunteerSignupService(
            UserRepository userRepository,
            EventRepository eventRepository,
            VolunteerRoleRepository roleRepository,
            EventDateRepository eventDateRepository,
            VolunteerSignupRepository signupRepository
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
        this.eventDateRepository = eventDateRepository;
        this.signupRepository = signupRepository;
    }

    public VolunteerSignup signup(UUID userId, UUID roleId, UUID eventDateId) {
        if (signupRepository.existsByUser_IdAndRole_IdAndEventDate_Id(userId, roleId, eventDateId)) {
            throw new ResourceConflictException("User is already signed up for this role and date");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        VolunteerRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        EventDate eventDate = eventDateRepository.findById(eventDateId)
                .orElseThrow(() -> new EntityNotFoundException("Event date not found"));

        if (!role.getEvent().getId().equals(eventDate.getEvent().getId())) {
            throw new BusinessRuleException("Role and event date must belong to the same event");
        }

        validateRoleCapacity(role, eventDate);

        VolunteerSignup signup = new VolunteerSignup();
        signup.setUser(user);
        signup.setRole(role);
        signup.setEventDate(eventDate);
        signup.setStatus(SignupStatus.CONFIRMED);

        return signupRepository.save(signup);
    }

    @Transactional(readOnly = true)
    public List<VolunteerSignup> getSignupsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        return signupRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<VolunteerSignup> getSignupsByEventId(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Event not found");
        }

        return signupRepository.findByEventDate_Event_Id(eventId);
    }

    @Transactional(readOnly = true)
    public int getRemainingSlots(UUID roleId, UUID eventDateId) {
        VolunteerRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        long currentSignups = signupRepository.countByRoleIdAndEventDateId(roleId, eventDateId);

        return Math.max(0, role.getRequiredPeople() - Math.toIntExact(currentSignups));
    }

    private void validateRoleCapacity(VolunteerRole role, EventDate eventDate) {
        long currentSignups = signupRepository.countByRoleIdAndEventDateId(
                role.getId(),
                eventDate.getId()
        );

        if (currentSignups >= role.getRequiredPeople()) {
            throw new BusinessRuleException("No more slots available for this role");
        }
    }
}
