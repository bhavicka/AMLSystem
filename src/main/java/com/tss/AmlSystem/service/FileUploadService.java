package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.response.FileUploadProcessDto;
import com.tss.AmlSystem.entity.enums.tenant.FileStatus;
import com.tss.AmlSystem.entity.enums.tenant.FileType;
import com.tss.AmlSystem.entity.tenant.File;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.repository.FileRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.repository.UserCredentialRepository;
import com.tss.AmlSystem.strategy.FileHeaderValidator;
import com.tss.AmlSystem.strategy.FileHeaderValidatorFactory;
import com.tss.AmlSystem.strategy.FileJobLauncher;
import com.tss.AmlSystem.strategy.FileJobLauncherFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class FileUploadService {

    private final FileRepository fileRepository;
    private final TenantUserRepository tenantUserRepository;

    private final FileJobLauncherFactory launcherFactory;
    private final FileHeaderValidatorFactory validatorFactory;

    public FileUploadProcessDto uploadFile(MultipartFile multipartFile, FileType fileType) throws Exception {
        String tenant = TenantContext.getCurrentTenant();
        System.out.println(tenant);

        FileHeaderValidator validator = validatorFactory.getValidator(fileType);
        validator.validate(multipartFile);

        System.out.println("header validated");

        Path storedFilePath = storeFile(multipartFile,fileType);
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
        file = fileRepository.save(file);

        System.out.println("file stored");

        FileJobLauncher launcher=launcherFactory.getLauncher(fileType);

        launcher.launch(storedFilePath.toString(), file.getId(), tenant);

        return new FileUploadProcessDto(
                "Customer batch job launched",
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
}
