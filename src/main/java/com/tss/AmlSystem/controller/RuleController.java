package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.RuleAssignmentDto;
import com.tss.AmlSystem.service.TenantRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RuleController {

    private final TenantRuleService tenantRuleService;

    @PostMapping("/rules/assign")
    public ResponseEntity<String> assignRules(@RequestBody@Valid RuleAssignmentDto ruleAssignmentDto){
        if(tenantRuleService.assignRules(ruleAssignmentDto))
            return ResponseEntity.ok("Rules assigned successfully");
        else
            return ResponseEntity.badRequest().body("One or more rule codes are invalid");
    }
}
