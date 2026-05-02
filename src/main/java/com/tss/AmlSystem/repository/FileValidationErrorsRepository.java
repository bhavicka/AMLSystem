package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileValidationErrorsRepository extends JpaRepository<FileValidationErrors,Long> {
}
