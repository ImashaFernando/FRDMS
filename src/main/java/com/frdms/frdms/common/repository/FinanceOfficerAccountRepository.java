package com.frdms.frdms.common.repository;

import com.frdms.frdms.common.entity.FinanceOfficerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceOfficerAccountRepository
        extends JpaRepository<FinanceOfficerAccount, Long> {

    Optional<FinanceOfficerAccount> findByEmail(String email);
}