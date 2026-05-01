package com.serve.repository;

import com.serve.domain.VolunteerRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VolunteerRoleRepository extends JpaRepository<VolunteerRole, UUID> {
}
