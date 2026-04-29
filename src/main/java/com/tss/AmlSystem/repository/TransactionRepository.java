package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Account;
import com.tss.AmlSystem.entity.tenant.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findByAccountNumber(String accountNumber);
    List<Transaction> findByCounterPartyAccountNumber(String counterPartyAccountNumber);
}
