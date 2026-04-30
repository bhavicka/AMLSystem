package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.CaseEscalateDto;
import com.tss.AmlSystem.dto.request.CaseRequestDto;
import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.service.CaseService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CaseDashboardDto>> getAllCases(){
        return new ResponseEntity<>(caseService.getAllCases(), HttpStatus.OK);
    }

    @GetMapping("/cases/{caseReferenceNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity<CaseDetailDto> getCaseDetail(@PathVariable String caseReferenceNumber) {
        return new ResponseEntity<>(caseService.getCaseDetail(caseReferenceNumber), HttpStatus.OK);
    }

    @PutMapping("/cases/{caseReferenceNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public ResponseEntity changeCaseStatus(
            @RequestBody(required = false) CaseEscalateDto caseEscalateDto
    ) {
        if(caseEscalateDto.getAction().equalsIgnoreCase("dismiss")){
            return ResponseEntity.ok(caseService.dismissCase(caseEscalateDto));
        } else if(caseEscalateDto.getAction().equalsIgnoreCase("escalate")){
            byte[] pdfBytes = caseService.escalateCase(caseEscalateDto);

            // Set headers to trigger a file download in the browser
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=STR_Report.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
