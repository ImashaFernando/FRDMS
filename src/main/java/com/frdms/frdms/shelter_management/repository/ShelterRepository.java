package com.frdms.frdms.shelter_management.repository;

import com.frdms.frdms.shelter_management.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelterRepository
        extends JpaRepository<Shelter, Long> {

    boolean existsByLocationIgnoreCase(String location);

    List<Shelter> findByDisasterNameIgnoreCaseAndStatus(
            String disasterName,
            Shelter.ShelterStatus status
    );
}