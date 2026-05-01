package com.serve.repository;

import com.serve.domain.VolunteerSignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VolunteerSignupRepository extends JpaRepository<VolunteerSignup, UUID> {
}
