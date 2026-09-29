package com.frdms.frdms.disaster_management.repository;

import com.frdms.frdms.disaster_management.entity.Disaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisasterRepository
        extends JpaRepository<Disaster, Long> {

    // Search flood events by event name
    List<Disaster> findByEventNameContainingIgnoreCase(
            String eventName
    );
}