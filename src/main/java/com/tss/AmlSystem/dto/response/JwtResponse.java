package com.tss.AmlSystem.dto.response;

public record JwtResponse (
        String jwt,
        String prefix,
        String refreshToken,
        String email,
        String bankName,
        String role
){}
