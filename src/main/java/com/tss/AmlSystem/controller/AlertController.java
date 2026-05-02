package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.dto.response.GeneratedAlertDto;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping("/alerts")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<Slice<GeneratedAlertDto>> getAlertDashboard(
            @RequestParam(required = false) AlertStatus alertStatus,
            @RequestParam(required = false) String alertNumber,
            @PageableDefault()Pageable pageable
    ){
        return ResponseEntity.ok(alertService.getAlertDashboard(alertStatus,alertNumber,pageable));
    }

    @GetMapping("/alerts/{alertNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity<AlertDetailDto> getAlertDetail(@PathVariable String alertNumber){
        return ResponseEntity.ok(alertService.getAlertDetail(alertNumber));
    }
}
