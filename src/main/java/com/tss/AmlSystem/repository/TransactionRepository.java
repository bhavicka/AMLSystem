package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.transactionDate >= :lookBack
        ORDER BY t.transactionDate DESC
    """)
    List<Transaction> findRecentTransactions(LocalDate lookBack);
}
