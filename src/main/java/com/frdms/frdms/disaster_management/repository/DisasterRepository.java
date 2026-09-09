package com.frdms.frdms.disaster_management.repository;

import com.frdms.frdms.disaster_management.entity.Disaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisasterRepository extends JpaRepository<Disaster, Long> {
}