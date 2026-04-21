package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.TenantRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;

import java.time.LocalDateTime;

public interface AlertRepository extends JpaRepository<Alert,Long> {
    @Query("""
           SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
           FROM Alert a
           JOIN a.transactions t
           WHERE t.accountNumber = :accountNumber
             AND a.tenantRule = :tenantRule
             AND a.createdAt > :since
             AND a.status = com.tss.AmlSystem.entity.enums.tenant.AlertStatus.NEW
           """)
    boolean existsByAccountNumberAndTenantRuleAndGeneratedAtAfter(
            @Param("accountNumber") String accountNumber,
            @Param("tenantRule") TenantRule tenantRule,
            @Param("since") LocalDateTime since
    );

//    @Query(value = """
//    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
//    FROM alerts a
//    JOIN transactions t ON t.alert_id = a.id
//    WHERE t.account_number = :accountNumber
//      AND a.tenant_rule_id = :tenantRule
//      AND a.created_at > :since
//      AND a.status = 'NEW'
//    """, nativeQuery = true)
//    boolean existsByAccountNumberAndTenantRuleAndGeneratedAtAfter(
//            @Param("accountNumber") String accountNumber,
//            @Param("tenantRule") Long tenantRule,
//            @Param("since") LocalDateTime since
//    );
}
