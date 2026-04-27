package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File,Long> {
    Optional<File> findByFileHash(String fileHash);
}
