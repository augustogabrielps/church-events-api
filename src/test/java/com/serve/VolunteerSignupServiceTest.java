package com.serve;

import com.serve.domain.*;
import com.serve.repository.*;
import com.serve.service.VolunteerSignupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static com.serve.domain.EventStatus.OPEN;
import static com.serve.domain.UserRole.VOLUNTEER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VolunteerSignupServiceTest {

    @Autowired
    private VolunteerSignupService signupService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventDateRepository eventDateRepository;

    @Autowired
    private VolunteerRoleRepository roleRepository;

    @Test
    void shouldCreateSignupSuccessfully() {

        User user = new User();
        user.setName("Test");
        user.setEmail("test@email.com");
        user.setRole(VOLUNTEER);
        user = userRepository.save(user);

        Event event = new Event();
        event.setTitle("Test Event");
        event.setStatus(OPEN);
        event = eventRepository.save(event);

        EventDate date = new EventDate();
        date.setEvent(event);
        date.setEventDate(java.time.LocalDate.now());
        date = eventDateRepository.save(date);

        VolunteerRole role = new VolunteerRole();
        role.setEvent(event);
        role.setName("Kitchen");
        role.setRequiredPeople(2);
        role = roleRepository.save(role);

        VolunteerSignup signup = signupService.signup(
                user.getId(),
                role.getId(),
                date.getId()
        );

        assertNotNull(signup.getId());
    }
}