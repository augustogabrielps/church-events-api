package com.serve.service;

import com.serve.domain.Event;
import com.serve.domain.EventDate;
import com.serve.domain.User;
import com.serve.domain.VolunteerRole;
import com.serve.domain.VolunteerSignup;
import com.serve.exception.BusinessRuleException;
import com.serve.exception.ResourceConflictException;
import com.serve.repository.EventDateRepository;
import com.serve.repository.EventRepository;
import com.serve.repository.UserRepository;
import com.serve.repository.VolunteerRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.serve.domain.EventStatus.CANCELLED;
import static com.serve.domain.EventStatus.CLOSED;
import static com.serve.domain.EventStatus.OPEN;
import static com.serve.domain.UserRole.VOLUNTEER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
        User user = createUser("volunteer-success@email.com");
        Event event = createEvent("Success Event");
        EventDate eventDate = createEventDate(event);
        VolunteerRole role = createRole(event, 2);

        VolunteerSignup signup = signupService.signup(
                user.getId(),
                role.getId(),
                eventDate.getId()
        );

        assertNotNull(signup.getId());
    }

    @Test
    void shouldFailWhenRoleIsFull() {
        User firstUser = createUser("volunteer-first@email.com");
        User secondUser = createUser("volunteer-second@email.com");
        Event event = createEvent("Full Role Event");
        EventDate eventDate = createEventDate(event);
        VolunteerRole role = createRole(event, 1);

        signupService.signup(firstUser.getId(), role.getId(), eventDate.getId());

        assertThrows(
                BusinessRuleException.class,
                () -> signupService.signup(secondUser.getId(), role.getId(), eventDate.getId())
        );
    }

    @Test
    void shouldFailWhenUserSignsUpTwice() {
        User user = createUser("volunteer-duplicate@email.com");
        Event event = createEvent("Duplicate Signup Event");
        EventDate eventDate = createEventDate(event);
        VolunteerRole role = createRole(event, 2);

        signupService.signup(user.getId(), role.getId(), eventDate.getId());

        assertThrows(
                ResourceConflictException.class,
                () -> signupService.signup(user.getId(), role.getId(), eventDate.getId())
        );
    }

    @Test
    void shouldFailWhenRoleAndDateBelongToDifferentEvents() {
        User user = createUser("volunteer-mismatch@email.com");
        Event roleEvent = createEvent("Role Event");
        Event dateEvent = createEvent("Date Event");
        EventDate eventDate = createEventDate(dateEvent);
        VolunteerRole role = createRole(roleEvent, 2);

        assertThrows(
                BusinessRuleException.class,
                () -> signupService.signup(user.getId(), role.getId(), eventDate.getId())
        );
    }

    @Test
    void shouldAllowSignupWhenEventIsOpen() {
        User user = createUser("volunteer-open@email.com");
        Event event = createEvent("Open Event");
        EventDate eventDate = createEventDate(event);
        VolunteerRole role = createRole(event, 1);

        VolunteerSignup signup = signupService.signup(
                user.getId(),
                role.getId(),
                eventDate.getId()
        );

        assertNotNull(signup.getId());
    }

    @Test
    void shouldRejectSignupWhenEventIsClosed() {
        User user = createUser("volunteer-closed@email.com");
        Event event = createEvent("Closed Event");
        event.setStatus(CLOSED);
        event = eventRepository.save(event);
        EventDate eventDate = createEventDate(event);
        VolunteerRole role = createRole(event, 1);

        assertThrows(
                BusinessRuleException.class,
                () -> signupService.signup(user.getId(), role.getId(), eventDate.getId())
        );
    }

    @Test
    void shouldRejectSignupWhenEventIsCancelled() {
        User user = createUser("volunteer-cancelled@email.com");
        Event event = createEvent("Cancelled Event");
        event.setStatus(CANCELLED);
        event = eventRepository.save(event);
        EventDate eventDate = createEventDate(event);
        VolunteerRole role = createRole(event, 1);

        assertThrows(
                BusinessRuleException.class,
                () -> signupService.signup(user.getId(), role.getId(), eventDate.getId())
        );
    }

    private User createUser(String email) {
        User user = new User();
        user.setName("Test Volunteer");
        user.setEmail(email);
        user.setRole(VOLUNTEER);

        return userRepository.save(user);
    }

    private Event createEvent(String title) {
        Event event = new Event();
        event.setTitle(title);
        event.setStatus(OPEN);

        return eventRepository.save(event);
    }

    private EventDate createEventDate(Event event) {
        EventDate eventDate = new EventDate();
        eventDate.setEvent(event);
        eventDate.setEventDate(LocalDate.now());

        return eventDateRepository.save(eventDate);
    }

    private VolunteerRole createRole(Event event, int requiredPeople) {
        VolunteerRole role = new VolunteerRole();
        role.setEvent(event);
        role.setName("Kitchen");
        role.setRequiredPeople(requiredPeople);

        return roleRepository.save(role);
    }
}
