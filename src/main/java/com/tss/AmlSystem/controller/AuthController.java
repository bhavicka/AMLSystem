package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.request.*;
import com.tss.AmlSystem.dto.response.ComplianceOfficerRegisteredDto;
import com.tss.AmlSystem.dto.response.JwtResponse;
import com.tss.AmlSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import com.tss.AmlSystem.entity.enums.LogTag;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/banks/register")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<String> registerBank(@RequestBody@Valid BankRegisterDto bankRegisterDto){
        log.info("{} Received request to register bank: {}", LogTag.SYSTEM.getValue(), bankRegisterDto.bankName());
        return ResponseEntity.ok(authService.registerBank(bankRegisterDto));
    }

    @PostMapping("/bank-officers/register")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<ComplianceOfficerRegisteredDto> registerComplianceOfficer(@RequestBody@Valid ComplianceOfficerRegisterDto complianceOfficerRegisterDto){
        log.info("{} Received request to register Compliance Officer", LogTag.SYSTEM.getValue());
        return ResponseEntity.ok(authService.registerComplianceOfficer(complianceOfficerRegisterDto));
    }

    @PostMapping("/update-password")
    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    public ResponseEntity<String> updatePassword(@RequestBody@Valid PasswordChangeRequestDto request){
        log.info("{} Received request to update password for user: {}", LogTag.SYSTEM.getValue(), request.email());
        if(authService.updatePassword(request))
            return ResponseEntity.ok("Password updated successfully");
        else {
            log.warn("{} Password update rejected for user: {}", LogTag.AUTH.getValue(), request.email());
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody@Valid LoginRequest loginRequest){
        log.info("{} Received login request for user: {}", LogTag.SYSTEM.getValue(), loginRequest.email());
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody@Valid TokenRefreshRequest request) {
        log.info("{} Received refresh token request", LogTag.SYSTEM.getValue());
        return ResponseEntity.ok(authService.refreshToken(request));
    }

}
