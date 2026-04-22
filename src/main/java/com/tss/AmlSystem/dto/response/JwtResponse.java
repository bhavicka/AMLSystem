package com.tss.AmlSystem.dto.response;

import java.util.List;

public record JwtResponse (
        String jwt,
        String prefix,
        String refreshToken,
        String email,
        String bankName,
        List<String> role
){}
