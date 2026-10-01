package com.frdms.frdms.volunteer_management.repository;

import com.frdms.frdms.volunteer_management.entity.VolunteerAccount;
import com.frdms.frdms.volunteer_management.entity.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerProfileRepository
        extends JpaRepository<VolunteerProfile, Long> {

    Optional<VolunteerProfile> findByVolunteerAccount(
            VolunteerAccount volunteerAccount
    );

    boolean existsByVolunteerAccount(
            VolunteerAccount volunteerAccount
    );

    List<VolunteerProfile> findAllByOrderByIdDesc();
}