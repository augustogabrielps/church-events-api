package com.serve.repository;

import com.serve.domain.EventDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventDateRepository extends JpaRepository<EventDate, UUID> {
}
