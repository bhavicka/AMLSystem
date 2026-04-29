package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.response.TenantDashboardDto;
import com.tss.AmlSystem.dto.response.TenantDetailsDto;
import com.tss.AmlSystem.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping("/tenants")
    public ResponseEntity<TenantDashboardDto> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/tenants/{bankName}")
    public ResponseEntity<TenantDetailsDto> getTenantByBankName(@PathVariable String bankName) {
        return ResponseEntity.ok(tenantService.getTenantByBankName(bankName));
    }

}
