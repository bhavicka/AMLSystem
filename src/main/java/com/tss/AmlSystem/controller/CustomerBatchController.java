package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.entity.enums.tenant.BatchStatus;
import com.tss.AmlSystem.entity.tenant.Batch;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.repository.BatchRepository;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.service.BatchJobLauncherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer-batch")
public class CustomerBatchController {

    private final BatchRepository batchRepository;
    private final FileRepository fileRepository;
    private final TenantUserRepository tenantUserRepository;
    private final BatchJobLauncherService batchJobLauncherService;

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> uploadCustomerFile(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("uploadedBy") Long uploadedBy,
            @RequestParam(value = "tenant", required = false) String tenant
    ) throws Exception {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is required"));
        }

        if (!StringUtils.hasText(multipartFile.getOriginalFilename())
                || !multipartFile.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only CSV files are supported"));
        }

        if (StringUtils.hasText(tenant)) {
            TenantContext.setCurrentTenant(tenant.trim());
        }

        try {
            TenantUser tenantUser = tenantUserRepository.findById(uploadedBy)
                    .orElseThrow(() -> new EntityNotFoundException("Tenant user not found: " + uploadedBy));

            Path storedFilePath = storeFile(multipartFile);
            int totalRecords = countDataRows(storedFilePath);

            Batch batch = new Batch();
            batch.setUploadedBy(tenantUser);
            batch.setCreatedAt(LocalDateTime.now());
            batch = batchRepository.save(batch);

            File file = new File();
            file.setBatch(batch);
            file.setFileName(multipartFile.getOriginalFilename());
            file.setFileStoragePath(storedFilePath.toString());
            file.setFileSizeBytes(multipartFile.getSize());
            file.setTotalRecords(totalRecords);
            file.setStatus(BatchStatus.UPLOADED);
            file.setCreatedAt(LocalDateTime.now());
            file = fileRepository.save(file);

            JobExecution jobExecution = batchJobLauncherService.launchCustomerJob(storedFilePath.toString(), file.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Customer batch job launched");
            response.put("jobExecutionId", jobExecution.getId());
            response.put("fileId", file.getId());
            response.put("batchId", batch.getId());
            response.put("storedPath", storedFilePath.toString());
            response.put("totalRecords", totalRecords);
            response.put("status", file.getStatus());

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } finally {
            TenantContext.clear();
        }
    }

    private Path storeFile(MultipartFile multipartFile) throws IOException {
        Path uploadDir = Path.of("uploads", "customer");
        Files.createDirectories(uploadDir);

        String originalName = multipartFile.getOriginalFilename();
        String safeName = UUID.randomUUID() + "-" + originalName.replace(" ", "_");
        Path storedPath = uploadDir.resolve(safeName);

        Files.copy(multipartFile.getInputStream(), storedPath, StandardCopyOption.REPLACE_EXISTING);
        return storedPath.toAbsolutePath();
    }

    private int countDataRows(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            long count = lines.skip(1)
                    .filter(StringUtils::hasText)
                    .count();
            return Math.toIntExact(count);
        }
    }
}
