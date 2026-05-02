package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.StrFilling;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StrFilingRepository extends JpaRepository<StrFilling, Long> {
    Slice<StrFilling> findByFiledBy(Pageable pageable, @NotNull TenantUser filedBy);
    Optional<StrFilling> findByReferenceNumber(String referenceNumber);

}
