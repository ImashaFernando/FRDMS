package com.frdms.frdms.finance_management.repository;

import com.frdms.frdms.finance_management.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget> findByStatus(String status);

    // Find budgets belonging to a specific disaster
    List<Budget> findByDisasterName(String disasterName);
}