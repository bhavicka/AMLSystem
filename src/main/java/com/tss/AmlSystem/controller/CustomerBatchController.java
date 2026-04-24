package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.service.CustomerBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer-batch")
public class CustomerBatchController {

    private final CustomerBatchService customerBatchService;

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
            Map<String, Object> response = customerBatchService.uploadAndProcess(multipartFile, uploadedBy);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } finally {
            TenantContext.clear();
        }
    }
}
