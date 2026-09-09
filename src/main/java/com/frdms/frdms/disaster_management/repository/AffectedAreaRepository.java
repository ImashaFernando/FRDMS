package com.frdms.frdms.disaster_management.repository;

import com.frdms.frdms.disaster_management.entity.AffectedArea;
import com.frdms.frdms.disaster_management.entity.Disaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffectedAreaRepository
        extends JpaRepository<AffectedArea, Long> {

    // Get all affected areas belonging to a specific flood event
    List<AffectedArea> findByDisaster(Disaster disaster);
}