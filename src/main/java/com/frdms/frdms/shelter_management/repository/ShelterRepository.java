package com.frdms.frdms.shelter_management.repository;

import com.frdms.frdms.shelter_management.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelterRepository
        extends JpaRepository<Shelter, Long> {

    boolean existsByLocationIgnoreCase(String location);
}