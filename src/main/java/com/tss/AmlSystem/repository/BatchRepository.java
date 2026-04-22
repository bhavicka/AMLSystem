package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<Batch, Long> {
}
