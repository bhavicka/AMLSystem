package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.CaseEscalateDto;
import com.tss.AmlSystem.dto.request.CaseRequestDto;
import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;
import com.tss.AmlSystem.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ResponseEntity<Slice<CaseDashboardDto>> getAllCases(
            @RequestParam(required = false) CaseStatus caseStatus,
            @RequestParam(required = false) String assignedToEmail,
            @RequestParam(required = false) String caseReferenceNumber,
            @PageableDefault() Pageable pageable
    ){
        return new ResponseEntity<>(caseService.getAllCases(assignedToEmail,caseStatus,caseReferenceNumber,pageable), HttpStatus.OK);
    }

    @GetMapping("/cases/{caseReferenceNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity<CaseDetailDto> getCaseDetail(@PathVariable String caseReferenceNumber) {
        return new ResponseEntity<>(caseService.getCaseDetail(caseReferenceNumber), HttpStatus.OK);
    }

    @PutMapping("/cases")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity changeCaseStatus(
            @RequestBody(required = false) CaseEscalateDto caseEscalateDto
    ) {
        if(caseEscalateDto.getAction().equalsIgnoreCase("dismiss")){
            return ResponseEntity.ok(caseService.dismissCase(caseEscalateDto));
        } else if(caseEscalateDto.getAction().equalsIgnoreCase("escalate")){
            return ResponseEntity.ok(caseService.escalateCase(caseEscalateDto));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
