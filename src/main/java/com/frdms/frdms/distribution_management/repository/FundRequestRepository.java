
package com.frdms.frdms.distribution_management.repository;

import com.frdms.frdms.distribution_management.entity.FundRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FundRequestRepository
        extends JpaRepository<FundRequest, Long> {

    Optional<FundRequest> findByResourceRequestId(
            Long resourceRequestId
    );

    boolean existsByResourceRequestId(
            Long resourceRequestId
    );

    List<FundRequest> findByStatus(String status);

    List<FundRequest> findByDisasterName(String disasterName);

    List<FundRequest> findByIdInAndStatus(
            List<Long> ids,
            String status
    );

    // Find fund requests linked to a particular budget
    List<FundRequest> findByBudgetId(Long budgetId);
}

