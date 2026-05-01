package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;
import com.tss.AmlSystem.entity.tenant.Case;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case,Long> {

    @Query("""
        SELECT c FROM Case c JOIN TenantUser u ON c.assignedTo.id = u.id WHERE u.email = :email
    """)
    List<Case> findAllByAssignedToEmail(String email);

    @Query("SELECT c FROM Case c JOIN FETCH c.assignedTo WHERE c.caseReferenceNumber = :caseRefNumber ")
    Optional<Case> findByCaseReferenceNumber(String caseRefNumber);

    @Query("""
    SELECT c FROM Case c
    LEFT JOIN FETCH c.assignedTo a
    WHERE c.status = COALESCE(:caseStatus, c.status)
      AND (a.email = :assignedToEmail OR :assignedToEmail = '' OR :assignedToEmail IS NULL)
      AND (LOWER(c.caseReferenceNumber) LIKE LOWER(CONCAT(:caseReferenceNumber, '%')) OR :caseReferenceNumber = '' OR :caseReferenceNumber IS NULL)
""")
    Slice<Case> searchCases(
            @Param("assignedToEmail") String assignedToEmail,
            @Param("caseReferenceNumber") String caseReferenceNumber,
            @Param("caseStatus") CaseStatus caseStatus,
            Pageable pageable);
}
