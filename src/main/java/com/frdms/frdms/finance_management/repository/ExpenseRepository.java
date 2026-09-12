package com.frdms.frdms.finance_management.repository;

import com.frdms.frdms.finance_management.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByDescriptionContainingIgnoreCase(String description);

    List<Expense> findByCategory(String category);
}