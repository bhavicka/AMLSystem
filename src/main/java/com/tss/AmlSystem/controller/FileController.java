package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.FileUploadDto;
import com.tss.AmlSystem.dto.response.FileUploadProcessDto;
import com.tss.AmlSystem.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<FileUploadProcessDto> uploadFile(
            @RequestPart("file") MultipartFile multipartFile,
            @RequestPart("fileUploadDto") FileUploadDto fileUploadDto) throws Exception {
        System.out.println("here");
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!StringUtils.hasText(multipartFile.getOriginalFilename())
                || !multipartFile.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(fileUploadService.uploadFile(multipartFile, fileUploadDto.getFileType()));
    }
}
