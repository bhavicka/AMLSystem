package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert,Long> {

    @Query("""
    SELECT a FROM Alert a
    WHERE a.status = COALESCE(:alertStatus, a.status)
      AND (LOWER(a.alertNumber) LIKE LOWER(CONCAT(:alertNumber, '%')) OR :alertNumber = '' OR :alertNumber IS NULL)
    ORDER BY a.clientNumber
""")
    Slice<Alert> searchAlerts(
            @Param("alertStatus") AlertStatus alertStatus,
            @Param("alertNumber") String alertNumber,
            Pageable pageable);

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


    @Query("SELECT a FROM Alert a LEFT JOIN FETCH a.transactions WHERE a.alertNumber = :alertNumber")
    Optional<Alert> findAlertByAlertNumber(String alertNumber);
}
