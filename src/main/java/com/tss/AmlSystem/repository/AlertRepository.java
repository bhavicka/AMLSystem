package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Case;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.models.AlertDetailProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert,Long> {

    @Query("SELECT a FROM Alert a WHERE a.caseId.id = :caseId")
    List<Alert> findAllByCaseId(Long caseId);

    Optional<Alert> findByAlertNumber(String alertNumber);

    @EntityGraph(attributePaths = {"tenantRule"})
    List<Alert> findAll();

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Alert a
    WHERE a.alertHash = :hash
""")
    boolean existsByAlertHash(String hash);

    @Query("""
    SELECT
        a.alertNumber AS alertNumber,
        r.ruleName AS brokenRuleName,
        r.severityRate AS severity,
        a.status AS status,

        c.caseReferenceNumber AS caseReferenceNumber,
        c.status AS caseStatus,
        u.email AS assignedTo,
        SUM(t.amount) AS totalAmount,

        COUNT(t) AS transactionCount

    FROM Alert a
    JOIN a.tenantRule r
    LEFT JOIN a.caseId c
    LEFT JOIN c.assignedTo u
    LEFT JOIN a.transactions t

    WHERE a.id = :alertId

    GROUP BY
        a.alertNumber, r.ruleName, r.severityRate, a.status,
        c.caseReferenceNumber, c.status, u.email
""")
    AlertDetailProjection findAlertDetail(@Param("alertId") Long alertId);

    @Query("""
    SELECT t FROM Alert a
    JOIN a.transactions t
    WHERE a.id = :alertId
""")
    List<Transaction> findTransactionsByAlertId(Long alertId);


    @Query("""
           SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
           FROM Alert a
           WHERE a.clientNumber = :clientNumber
             AND a.tenantRule.id = :ruleId
             AND a.createdAt >= :lookbackStart
           """)
    boolean existsByClientNumberAndTenantRuleIdAndCreatedAfter(
            @Param("clientNumber") String clientNumber,
            @Param("ruleId") Long ruleId,
            @Param("lookbackStart") LocalDateTime lookbackStart
    );

    @Query("SELECT a.id FROM Alert a WHERE a.alertNumber = :alertNumber")
    Optional<Long> findAlertIdByAlertNumber(@Param("alertNumber") String alertNumber);
}
