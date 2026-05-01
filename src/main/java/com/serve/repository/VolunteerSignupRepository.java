package com.serve.repository;

import com.serve.domain.VolunteerSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VolunteerSignupRepository extends JpaRepository<VolunteerSignup, UUID> {

    boolean existsByUser_IdAndRole_IdAndEventDate_Id(UUID userId, UUID roleId, UUID eventDateId);

    List<VolunteerSignup> findByUser_Id(UUID userId);

    List<VolunteerSignup> findByEventDate_Event_Id(UUID eventId);

    @Query("""
            select count(signup)
            from VolunteerSignup signup
            where signup.role.id = :roleId
              and signup.eventDate.id = :eventDateId
            """)
    long countByRoleIdAndEventDateId(
            @Param("roleId") UUID roleId,
            @Param("eventDateId") UUID eventDateId
    );
}
