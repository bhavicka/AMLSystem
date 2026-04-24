package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.master.TenantRuleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRuleAssignmentRepository extends JpaRepository<TenantRuleAssignment, Long> {
}
