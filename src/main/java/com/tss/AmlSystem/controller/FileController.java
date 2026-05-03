package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.FileUploadDto;
import com.tss.AmlSystem.dto.response.*;
import com.tss.AmlSystem.service.FileService;
import com.tss.AmlSystem.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import com.tss.AmlSystem.entity.enums.LogTag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileService fileService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<FileUploadProcessDto> uploadFile(
            @RequestPart("file") MultipartFile multipartFile,
            @RequestPart("fileUploadDto")@Valid FileUploadDto fileUploadDto) throws Exception {
        log.info("{} Received file upload request. FileType: {}", LogTag.SYSTEM.getValue(), fileUploadDto.getFileType());
        if (multipartFile.isEmpty()) {
            log.warn("{} Uploaded file is empty", LogTag.SYSTEM.getValue());
            return ResponseEntity.badRequest().build();
        }
        if (!StringUtils.hasText(multipartFile.getOriginalFilename())
                || !multipartFile.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            log.warn("{} Invalid file name or extension: {}", LogTag.SYSTEM.getValue(), multipartFile.getOriginalFilename());
            return ResponseEntity.badRequest().build();
        }
        log.info("{} Delegating file upload to FileUploadService", LogTag.SYSTEM.getValue());
        return ResponseEntity.ok(fileUploadService.uploadFile(multipartFile, fileUploadDto.getFileType()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<CustomSliceDto<FileInlineDto>> getAllFiles(Pageable pageable){
        return ResponseEntity.ok(new CustomSliceDto<>(fileService.getAllFiles(pageable)));
    }

    @GetMapping("/{fileNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<FileDetailDto> getAllFiles(@PathVariable String fileNumber){
        return ResponseEntity.ok(fileService.getFileDetails(fileNumber));
    }

    @GetMapping("/{fileNumber}/errors")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<CustomSliceDto<FileErrorInlineDto>> getErrorsForFile(@PathVariable String fileNumber, Pageable pageable){
        return ResponseEntity.ok(new CustomSliceDto<>(fileService.getErrorsForFile(fileNumber, pageable)));
    }

}
