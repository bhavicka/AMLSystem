package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/bank")
    public ResponseEntity<String> registerBank(@RequestBody BankRegisterDto bankRegisterDto){
        return ResponseEntity.ok(authService.registerBank(bankRegisterDto));
    }
}
