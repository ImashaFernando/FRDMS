package com.frdms.frdms.disaster_management.repository;

import com.frdms.frdms.disaster_management.entity.AffectedArea;
import com.frdms.frdms.disaster_management.entity.Disaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AffectedAreaRepository
        extends JpaRepository<AffectedArea, Long> {

    List<AffectedArea> findByDisaster(Disaster disaster);
}