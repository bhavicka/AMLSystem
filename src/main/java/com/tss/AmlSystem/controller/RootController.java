package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.service.RuleEngineService;
import com.tss.AmlSystem.service.TenantSchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RootController {
    private final TenantSchemaService tenantSchemaService;
    private final RuleEngineService ruleEngineService;

    @PostMapping
    public ResponseEntity<String> createSchema(@RequestParam String name){
        tenantSchemaService.createSchema(name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/run-engine")
    public ResponseEntity<String> runEngine(@RequestParam("lookBackDays") int lookBackDays){
        LocalDate lookBackDate = LocalDate.now().minusDays(lookBackDays);
        ruleEngineService.execute(lookBackDate);
        return new ResponseEntity<>("Rule engine executed", HttpStatus.OK);
    }
}
