package com.frdms.frdms.victim_management.repository;

import com.frdms.frdms.victim_management.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VictimRepository
        extends JpaRepository<Victim, Long> {

    // =========================================================
    // FIND VICTIM BY NIC
    // =========================================================

    Optional<Victim> findByNicNumber(
            String nicNumber
    );


    // =========================================================
    // COUNT VICTIMS REGISTERED FOR A DISASTER
    //
    // Used by Disaster Management / PM dashboard
    // =========================================================

    long countByDisaster_Id(
            Long disasterId
    );
}