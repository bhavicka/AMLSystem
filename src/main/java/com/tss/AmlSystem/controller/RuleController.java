package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.RulePermissionDto;
import com.tss.AmlSystem.dto.request.RuleParameterUpdateDto;
import com.tss.AmlSystem.dto.response.RuleDashboardDto;
import com.tss.AmlSystem.dto.response.RuleDetailDto;
import com.tss.AmlSystem.dto.response.RuleParameterUpdatedDto;
import com.tss.AmlSystem.service.RuleService;
import com.tss.AmlSystem.service.TenantRuleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RuleController {

    private final TenantRuleService tenantRuleService;
    private final RuleService ruleService;

    @PutMapping("/rules")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<String> permitRules(@RequestParam String ruleAction, @RequestBody@Valid RulePermissionDto rulePermissionDto){
        if(tenantRuleService.assignRules(ruleAction, rulePermissionDto))
            return ResponseEntity.ok("Rules "+ ruleAction + "ed" + " successfully");
        else
            return ResponseEntity.badRequest().body("One or more rule codes are invalid");
    }

    @GetMapping("/tenants/{bankName}/rules")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<RuleDashboardDto> getTenantRules(@PathVariable String bankName, @RequestParam(defaultValue = "true") String isActive){
        return ResponseEntity.ok(tenantRuleService.getTenantRulesByBankName(bankName, Boolean.valueOf(isActive)));
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<RuleDashboardDto> getAllRules(){
        return ResponseEntity.ok(ruleService.getMasterRulesList());
    }

    @GetMapping("/rules/{ruleCode}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<RuleDetailDto> getRuleDetails(@PathVariable String ruleCode){
        return ResponseEntity.ok(ruleService.getRuleDetails(ruleCode));
    }

    @GetMapping("/bank-rules")
    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    public ResponseEntity<RuleDashboardDto> getAllTenantRules(){
        return ResponseEntity.ok(tenantRuleService.getTenantRuleList());
    }

    @GetMapping("/bank-rules/{ruleCode}")
    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    public ResponseEntity<RuleDetailDto> getTenantRuleDetails(@PathVariable String ruleCode){
        return ResponseEntity.ok(tenantRuleService.getRuleDetails(ruleCode));
    }

    @PutMapping("/bank-rules/{ruleCode}/parameters")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<RuleParameterUpdatedDto> updateRuleParameters(@PathVariable@NotBlank String ruleCode, @RequestBody@Valid RuleParameterUpdateDto ruleParameterUpdateDto){
        return ResponseEntity.ok(tenantRuleService.updateRuleParameters(ruleCode, ruleParameterUpdateDto));
    }


}
