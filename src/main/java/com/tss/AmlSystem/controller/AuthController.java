package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.request.LoginRequest;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/bank")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<String> registerBank(@RequestBody BankRegisterDto bankRegisterDto){
        return ResponseEntity.ok(authService.registerBank(bankRegisterDto));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
