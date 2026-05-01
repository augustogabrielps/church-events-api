package com.serve.service;

import com.serve.domain.EventDate;
import com.serve.domain.User;
import com.serve.domain.VolunteerRole;
import com.serve.domain.VolunteerSignup;
import com.serve.repository.EventDateRepository;
import com.serve.repository.UserRepository;
import com.serve.repository.VolunteerRoleRepository;
import com.serve.repository.VolunteerSignupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class VolunteerSignupService {

    private static final String DEFAULT_SIGNUP_STATUS = "CONFIRMED";

    private final UserRepository userRepository;
    private final VolunteerRoleRepository roleRepository;
    private final EventDateRepository eventDateRepository;
    private final VolunteerSignupRepository signupRepository;

    public VolunteerSignupService(
            UserRepository userRepository,
            VolunteerRoleRepository roleRepository,
            EventDateRepository eventDateRepository,
            VolunteerSignupRepository signupRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.eventDateRepository = eventDateRepository;
        this.signupRepository = signupRepository;
    }

    public VolunteerSignup signup(UUID userId, UUID roleId, UUID eventDateId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        VolunteerRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        EventDate eventDate = eventDateRepository.findById(eventDateId)
                .orElseThrow(() -> new EntityNotFoundException("Event date not found"));

        VolunteerSignup signup = new VolunteerSignup();
        signup.setUser(user);
        signup.setRole(role);
        signup.setEventDate(eventDate);
        signup.setStatus(DEFAULT_SIGNUP_STATUS);

        return signupRepository.save(signup);
    }
}
