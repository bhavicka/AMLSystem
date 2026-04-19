package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.service.TenantSchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RootController {
    private final TenantSchemaService tenantSchemaService;
    @PostMapping
    public ResponseEntity<String> createSchema(@RequestParam String name){
        tenantSchemaService.createSchema(name);
        return ResponseEntity.ok().build();
    }
}
