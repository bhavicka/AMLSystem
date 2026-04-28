package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.response.AlertDashboardDto;
import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping("/alerts")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<AlertDashboardDto> getAlertDashboard(){
        return ResponseEntity.ok(alertService.getAlertDashboard());
    }

    @GetMapping("/alerts/{alertNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<AlertDetailDto> getAlertDetail(@PathVariable String alertNumber){
        return ResponseEntity.ok(alertService.getAlertDetail(alertNumber));
    }
}
