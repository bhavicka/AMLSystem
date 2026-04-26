package com.tss.AmlSystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RuleQueryRepository {
    @Query(value = """
        SELECT td.client_number
        FROM (
            SELECT a.client_number,
                   t.amount,
                   t.transaction_date
            FROM transactions t
            JOIN accounts a ON t.account_number = a.account_number
            WHERE t.transaction_type = 'CREDIT'
              AND t.transaction_date BETWEEN :lookBackStart AND :windowEnd
        ) AS td
        WHERE td.transaction_date BETWEEN :windowStart AND :windowEnd
          AND td.amount < :perTxnThreshold
        GROUP BY td.client_number
        HAVING COUNT(*) >= :minimumTxns
           AND SUM(td.amount) > :totalThreshold
        """, nativeQuery = true)
    List<String> findSuspiciousClientsForStructuring(
            LocalDateTime lookBackStart,
            LocalDateTime windowEnd,
            LocalDateTime windowStart,
            BigDecimal perTxnThreshold,
            int minimumTxns,
            BigDecimal totalThreshold
    );

    @Query(value = """
        SELECT
            t.id AS id,
            t.account_number AS accountNumber,
            t.amount AS amount,
            t.transaction_date AS transactionDate
        FROM transactions t
        JOIN accounts a ON t.account_number = a.account_number
        WHERE a.client_number = :client
          AND t.amount < :perTxnThreshold
          AND t.transaction_type = 'CREDIT'
          AND t.transaction_date BETWEEN :windowStart AND :windowEnd
        ORDER BY t.transaction_date DESC
        """, nativeQuery = true)
    List<Object[]> findFlaggedTransactionsForStructuring(
            String client,
            BigDecimal perTxnThreshold,
            LocalDateTime windowStart,
            LocalDateTime windowEnd
    );


    @Query(value = """
        SELECT td.client_number
        FROM (
            SELECT c.client_number,
                   c.monthly_income,
                   c.profession_multiplier,
                   t.amount,
                   t.transaction_date
            FROM customers c
            JOIN accounts a ON c.client_number = a.client_number
            JOIN transactions t ON t.account_number = a.account_number
            WHERE t.transaction_type = 'CREDIT'
              AND t.transaction_date BETWEEN :lookBackStart AND :windowEnd
        ) AS td
        WHERE td.transaction_date BETWEEN :windowStart AND :windowEnd
          AND td.monthly_income IS NOT NULL
          AND td.monthly_income >= :minIncome
        GROUP BY td.client_number, td.monthly_income, td.profession_multiplier
        HAVING COUNT(*) >= :minTxnCount
           AND SUM(td.amount) >= :minTotalTxnAmount
           AND SUM(td.amount) > (td.monthly_income * td.profession_multiplier * :multiplierThreshold)
        """, nativeQuery = true)
    List<String> findIncomeMismatchClients(
            LocalDateTime lookBackStart,
            LocalDateTime windowEnd,
            LocalDateTime windowStart,
            BigDecimal minIncome,
            int minTxnCount,
            BigDecimal minTotalTxnAmount,
            double multiplierThreshold
    );


    @Query(value = """
            SELECT t.id,t.account_number,t.amount,t.transaction_date
                FROM transactions t
                JOIN accounts a ON t.account_number=a.account_number
                WHERE a.client_number = :clientNumber
                  AND t.transaction_type = 'CREDIT'
                  AND t.transaction_date BETWEEN :windowStart AND :windowEnd
                ORDER BY t.transaction_date DESC
    """,nativeQuery = true)
    List<Object[]> findFlaggedTransactionsForIncomeMismatch(
            String clientNumber,
            LocalDateTime windowStart,
            LocalDateTime windowEnd
    );


}
