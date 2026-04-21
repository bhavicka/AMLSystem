package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@EnableMethodSecurity
public class AuthController {
    @PostMapping
    public ResponseEntity<String> registerBank(@RequestBody BankRegisterDto bankRegisterDto){

    }
}
