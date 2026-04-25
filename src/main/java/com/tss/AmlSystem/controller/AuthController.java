package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.request.*;
import com.tss.AmlSystem.dto.response.ComplianceOfficerRegisteredDto;
import com.tss.AmlSystem.dto.response.JwtResponse;
import com.tss.AmlSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/banks/register")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<String> registerBank(@RequestBody BankRegisterDto bankRegisterDto){
        return ResponseEntity.ok(authService.registerBank(bankRegisterDto));
    }

    @PostMapping("/bank-officers/register")
    @PreAuthorize("hasAuthority('BANK_ADMIN')")
    public ResponseEntity<ComplianceOfficerRegisteredDto> registerComplianceOfficer(@RequestBody ComplianceOfficerRegisterDto complianceOfficerRegisterDto){
        return ResponseEntity.ok(authService.registerComplianceOfficer(complianceOfficerRegisterDto));
    }

    @PostMapping("/update-password")
    @PreAuthorize("hasAuthority('COMPLIANCE_OFFICER') or hasAuthority('BANK_ADMIN')")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordChangeRequestDto request){
        if(authService.updatePassword(request))
            return ResponseEntity.ok("Password updated successfully");
        else
            return ResponseEntity.badRequest().body("Current password is incorrect");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

}
