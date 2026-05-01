package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.response.CustomSliceDto;
import com.tss.AmlSystem.dto.response.StrFilingDetailDto;
import com.tss.AmlSystem.dto.response.StrFilingInlineDto;
import com.tss.AmlSystem.service.StrFilingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/str-filings")
@RequiredArgsConstructor
public class StrFilingController {

    private final StrFilingService strFilingService;

    @GetMapping
    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    public ResponseEntity<CustomSliceDto<StrFilingInlineDto>> getAllStrFilings(Pageable pageable){
        return ResponseEntity.ok(
                new CustomSliceDto<>(
                        strFilingService.getAllStrFilings(pageable)
                )
        );
    }

    @GetMapping("/{referenceNumber}")
    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    public ResponseEntity<StrFilingDetailDto> getStrFilingDetails(@PathVariable String referenceNumber){
        return ResponseEntity.ok(strFilingService.getStrFilingDetails(referenceNumber));
    }

}
