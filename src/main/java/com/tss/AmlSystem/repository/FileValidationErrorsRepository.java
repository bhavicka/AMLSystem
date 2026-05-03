package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileValidationErrorsRepository extends JpaRepository<FileValidationErrors,Long> {
    Slice<FileValidationErrors> findByFileId(Long fileId, Pageable pageable);
}
