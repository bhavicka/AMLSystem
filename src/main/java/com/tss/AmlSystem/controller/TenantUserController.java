package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.response.TenantUserDashboardDto;
import com.tss.AmlSystem.dto.response.TenantUserProfileDto;
import com.tss.AmlSystem.service.TenantUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TenantUserController {

    private final TenantUserService tenantUserService;

    @GetMapping("/compliance-officers/{employeeCode}")
    public ResponseEntity<TenantUserProfileDto> getUserProfile(
            @PathVariable(required = false) String employeeCode
    ) {
        return ResponseEntity.ok(tenantUserService.getUserProfile(employeeCode));
    }

    @GetMapping("/compliance-officers")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<TenantUserDashboardDto> getAllComplianceOfficers(
    ) {
        return ResponseEntity.ok(tenantUserService.getAllComplianceOfficers());
    }
}
