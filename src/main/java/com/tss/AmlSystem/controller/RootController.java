package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.service.RuleEngineService;
import com.tss.AmlSystem.service.TenantSchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RootController {
    private final RuleEngineService ruleEngineService;

    @PostMapping("/run-engine")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<String> runEngine(){
        ruleEngineService.execute(TenantContext.getCurrentTenant());
        return new ResponseEntity<>("Rule engine executed", HttpStatus.OK);
    }

    @GetMapping("/test")
    public String test() {
        return "Controller is active";
    }
}
