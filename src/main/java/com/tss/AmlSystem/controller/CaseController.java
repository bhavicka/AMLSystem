package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.CaseRequestDto;
import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CaseController {
    private final CaseService caseService;

    @PostMapping("/cases")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<String> createCase(@RequestBody CaseRequestDto dto){
        caseService.createCase(dto.getAlertNumbers(),dto.getAssignedTo());
        return ResponseEntity.ok("Case created successfully");
    }

    @GetMapping("/cases")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity<List<CaseDashboardDto>> getAllCases(){
        return new ResponseEntity<>(caseService.getAllCases(), HttpStatus.OK);
    }

    @GetMapping("/cases/{caseReferenceNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity<CaseDetailDto> getCaseDetail(@PathVariable String caseReferenceNumber) {
        return new ResponseEntity<>(caseService.getCaseDetail(caseReferenceNumber), HttpStatus.OK);
    }




}
