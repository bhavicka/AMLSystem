package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.FileUploadDto;
import com.tss.AmlSystem.service.CustomerBatchService;
import com.tss.AmlSystem.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileUploadService fileUploadService;

    @PostMapping(
            value = "/upload"
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestPart("file") MultipartFile multipartFile,
            @RequestPart("fileUploadDto") FileUploadDto fileUploadDto)
    {
        System.out.println("here");

        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File is required"));
        }

        if (!StringUtils.hasText(multipartFile.getOriginalFilename())
                || !multipartFile.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only CSV files are supported"));
        }

        try {
            Map<String, Object> response = fileUploadService.uploadFile(multipartFile, fileUploadDto.getUploadedBy(),fileUploadDto.getFileType());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to process file: " + e.getMessage()));
        }
    }
}
