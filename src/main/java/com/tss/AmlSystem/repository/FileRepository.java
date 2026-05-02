package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.tenant.File;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<File,Long> {
    Optional<File> findByFileHash(String fileHash);

    @Query("SELECT f FROM File f ORDER BY f.createdAt DESC")
    Slice<File> findAllOrderByCreatedAtDesc(Pageable pageable);

    Optional<File> findByFileNumber(UUID fileNumber);
}
