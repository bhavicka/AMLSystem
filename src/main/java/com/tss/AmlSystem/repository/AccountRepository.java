package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByAccountNumber(String accountNumber);
}
