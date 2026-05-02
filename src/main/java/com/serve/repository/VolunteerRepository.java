package com.serve.repository;

import com.serve.domain.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VolunteerRepository extends JpaRepository<Volunteer, UUID> {
}
