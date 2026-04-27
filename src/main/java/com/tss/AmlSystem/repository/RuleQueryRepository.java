package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RuleQueryRepository extends JpaRepository<Transaction,Long> {
    @Query(value = """
    SELECT
        client_number,
        CAST(transaction_date AS DATE) AS window_end,
        CAST(transaction_date - INTERVAL '1 day' * :timeWindowInDays AS DATE) AS window_start
    FROM (
        SELECT 
            a.client_number,
            t.transaction_date,

            SUM(t.amount) OVER (
                PARTITION BY a.client_number
                ORDER BY t.transaction_date
                RANGE BETWEEN (:timeWindowInDays * INTERVAL '1 day') PRECEDING AND CURRENT ROW
            ) AS window_sum,

            COUNT(*) OVER (
                PARTITION BY a.client_number
                ORDER BY t.transaction_date
                RANGE BETWEEN (:timeWindowInDays * INTERVAL '1 day') PRECEDING AND CURRENT ROW
            ) AS window_count,

            t.amount

        FROM transactions t
        JOIN accounts a ON t.account_number = a.account_number

        WHERE t.transaction_type = 'CREDIT'
          AND t.transaction_date BETWEEN :lookBackStart AND :windowEnd
          AND t.amount < :perTxnThreshold

    ) sub

    WHERE window_count >= :minimumTxns
      AND window_sum > :totalThreshold
""", nativeQuery = true)
    List<Object[]> findStructuringSlidingWindow(
            @Param("lookBackStart") LocalDateTime lookBackStart,
            @Param("windowEnd") LocalDateTime windowEnd,
            @Param("perTxnThreshold") BigDecimal perTxnThreshold,
            @Param("minimumTxns") int minimumTxns,
            @Param("totalThreshold") BigDecimal totalThreshold,
            @Param("timeWindowInDays") int timeWindowInDays
    );

    @Query(value = """
        SELECT
            t.id AS id
        FROM transactions t
        JOIN accounts a ON t.account_number = a.account_number
        WHERE a.client_number = :client
          AND t.amount < :perTxnThreshold
          AND t.transaction_type = 'CREDIT'
          AND t.transaction_date BETWEEN :windowStart AND :windowEnd
        ORDER BY t.transaction_date DESC
        """, nativeQuery = true)
    List<Long> findFlaggedTransactionsForStructuring(
            String client,
            BigDecimal perTxnThreshold,
            LocalDate windowStart,
            LocalDate windowEnd
    );


    @Query(value = """
        SELECT client_number,
                CAST(transaction_date AS DATE) AS window_end,
                CAST(transaction_date - INTERVAL '1 day' * :timeWindowInDays AS DATE) AS window_start
        FROM (
            SELECT 
                c.client_number,t.transaction_date,

                SUM(t.amount) OVER (
                    PARTITION BY c.client_number
                    ORDER BY t.transaction_date
                    RANGE BETWEEN (:timeWindowInDays * INTERVAL '1 day') PRECEDING AND CURRENT ROW
                ) AS window_sum,

                COUNT(*) OVER (
                    PARTITION BY c.client_number
                    ORDER BY t.transaction_date
                    RANGE BETWEEN (:timeWindowInDays * INTERVAL '1 day') PRECEDING AND CURRENT ROW
                ) AS window_count,

                c.monthly_income,
                c.profession_multiplier

            FROM customers c
            JOIN accounts a ON c.client_number = a.client_number
            JOIN transactions t ON t.account_number = a.account_number

            WHERE t.transaction_type = 'CREDIT'
              AND t.transaction_date BETWEEN :lookBackStart AND :windowEnd
              AND c.monthly_income IS NOT NULL
              AND c.monthly_income >= :minIncome

        ) sub

        WHERE window_count >= :minTxnCount
          AND window_sum >= :minTotalTxnAmount
          AND window_sum > (monthly_income * profession_multiplier * :multiplierThreshold)
        """, nativeQuery = true)
    List<Object[]> findIncomeMismatchSlidingWindow(
            @Param("lookBackStart") LocalDateTime lookBackStart,
            @Param("windowEnd") LocalDateTime windowEnd,
            @Param("minIncome") BigDecimal minIncome,
            @Param("minTxnCount") int minTxnCount,
            @Param("minTotalTxnAmount") BigDecimal minTotalTxnAmount,
            @Param("multiplierThreshold") double multiplierThreshold,
            @Param("timeWindowInDays") int timeWindowInDays
    );


    @Query(value = """
            SELECT t.id
                FROM transactions t
                JOIN accounts a ON t.account_number=a.account_number
                WHERE a.client_number = :clientNumber
                  AND t.transaction_type = 'CREDIT'
                  AND t.transaction_date BETWEEN :windowStart AND :windowEnd
                ORDER BY t.transaction_date DESC
    """,nativeQuery = true)
    List<Long> findFlaggedTransactionsForIncomeMismatch(
            String clientNumber,
            LocalDate windowStart,
            LocalDate windowEnd
    );


}
