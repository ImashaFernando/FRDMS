
package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.ResourceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRequestRepository
        extends JpaRepository<ResourceRequest, Long> {

    boolean existsByDisasterNameAndItemName(
            String disasterName,
            String itemName
    );

    List<ResourceRequest> findAllByDisasterNameAndItemName(
            String disasterName,
            String itemName
    );
}
