package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.entity.enums.tenant.BatchStatus;
import com.tss.AmlSystem.entity.tenant.Batch;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.repository.BatchRepository;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

@Service
@RequiredArgsConstructor
public class CustomerBatchService {

    private final BatchRepository batchRepository;
    private final FileRepository fileRepository;
    private final TenantUserRepository tenantUserRepository;
    private final BatchJobLauncherService batchJobLauncherService;

    public Map<String, Object> uploadAndProcess(MultipartFile multipartFile, Long uploadedBy) throws Exception {
        String tenant = TenantContext.getCurrentTenant();
        TenantUser tenantUser = tenantUserRepository.findById(uploadedBy)
                .orElseThrow(() -> new EntityNotFoundException("Tenant user not found: " + uploadedBy));

        Path storedFilePath = storeFile(multipartFile);
        int totalRows = countDataRows(storedFilePath);

        Batch batch = new Batch();
        batch.setUploadedBy(tenantUser);
        batch = batchRepository.save(batch);

        File file = new File();
        file.setBatch(batch);
        file.setFileName(multipartFile.getOriginalFilename());
        file.setFileStoragePath(storedFilePath.toString());
        file.setFileSizeBytes(multipartFile.getSize());
        file.setTotalRecords(totalRows);
        file.setProcessedAt(LocalDateTime.now());
        file.setStatus(BatchStatus.UPLOADED);
        file.setCreatedAt(LocalDateTime.now());
        file = fileRepository.save(file);

        Long jobExecutionId = batchJobLauncherService.launchCustomerJob(storedFilePath.toString(), file.getId(), tenant);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Customer batch job launched");
        response.put("jobExecutionId", jobExecutionId);
        response.put("fileId", file.getId());
        response.put("batchId", batch.getId());
        response.put("totalRecords", totalRows);
        response.put("status", file.getStatus());

        return response;
    }

    private Path storeFile(MultipartFile multipartFile) throws IOException {
        Path uploadDir = Path.of("uploads", "customer");
        Files.createDirectories(uploadDir);

        String originalName = multipartFile.getOriginalFilename();
        String safeName = UUID.randomUUID() + "-" + (originalName != null ? originalName.replace(" ", "_") : "unnamed");
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
