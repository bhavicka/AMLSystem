package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
