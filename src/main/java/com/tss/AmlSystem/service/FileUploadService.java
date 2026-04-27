package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.response.FileUploadProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.FileStatus;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.strategy.batch.header.FileHeaderValidator;
import com.tss.AmlSystem.factory.FileHeaderValidatorFactory;
import com.tss.AmlSystem.strategy.batch.joblaunch.FileJobLauncher;
import com.tss.AmlSystem.factory.FileJobLauncherFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import com.tss.AmlSystem.entity.tenant.FileValidationErrors;
import com.tss.AmlSystem.repository.FileValidationErrorsRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final FileRepository fileRepository;
    private final TenantUserRepository tenantUserRepository;
    private final FileValidationErrorsRepository fileValidationErrorsRepository;

    private final FileJobLauncherFactory launcherFactory;
    private final FileHeaderValidatorFactory validatorFactory;

    public FileUploadProcessDto uploadFile(MultipartFile multipartFile, FileType fileType) throws Exception {
        String tenant = TenantContext.getCurrentTenant();
        log.info("{} Starting file upload process. Target FileType: {}, Tenant: {}", LogTag.BATCH.getValue(), fileType, tenant);

        String fileHash = calculateFileHash(multipartFile);
        fileRepository.findByFileHash(fileHash).ifPresent(existingFile -> {
            log.error("{} {} Duplicate file upload detected. FileHash: {}, Tenant: {}", LogTag.BATCH.getValue(), LogTag.SECURITY.getValue(), fileHash, tenant);
            throw new RuntimeException("Duplicate file upload detected: " + existingFile.getFileName());
        });

        Path storedFilePath = storeFile(multipartFile, fileType);
        int totalRows = countDataRows(storedFilePath);

        TenantUser uploadedBy = tenantUserRepository.findByEmail(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        ).orElseThrow();

        File file = new File();
        file.setUploadedBy(uploadedBy);
        file.setFileType(fileType);
        file.setFileName(multipartFile.getOriginalFilename());
        file.setFileStoragePath(storedFilePath.toString());
        file.setFileSizeBytes(multipartFile.getSize());
        file.setTotalRecords(totalRows);
        file.setStatus(FileStatus.UPLOADED);
        file.setCreatedAt(LocalDateTime.now());
        file.setFileHash(fileHash);
        file = fileRepository.save(file);

        try {
            FileHeaderValidator validator = validatorFactory.getValidator(fileType);
            validator.validate(multipartFile);
        } catch (IllegalArgumentException e) {
            log.warn("{} Initial header validation failed for file {}: {}", LogTag.BATCH.getValue(), file.getId(), e.getMessage());
            file.setStatus(FileStatus.FAILED);
            fileRepository.save(file);

            FileValidationErrors error = new FileValidationErrors();
            error.setFile(file);
            error.setRowNumber(1);
            error.setFieldName("Header");
            error.setErrorMessage("Invalid headers: " + e.getMessage());
            fileValidationErrorsRepository.save(error);

            throw e;
        }

        FileJobLauncher launcher = launcherFactory.getLauncher(fileType);
        log.info("{} Launching Spring Batch job for File ID: {} in Tenant: {}", LogTag.BATCH.getValue(), file.getId(), tenant);
        launcher.launch(storedFilePath.toString(), file.getId(), tenant);

        return new FileUploadProcessDto(
                fileType.name() + " batch job launched",
                file.getFileName(),
                totalRows,
                file.getStatus().toString()
        );
    }


    private Path storeFile(MultipartFile multipartFile,FileType fileType) throws IOException {
        Path uploadDir = Path.of("uploads", fileType.name().toLowerCase());
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

    private String calculateFileHash(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(file.getBytes());

        // Convert bytes to hex string
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
