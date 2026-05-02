package com.tss.AmlSystem.dto.response;

import java.util.List;

public record LoginResponseDto(
        String jwt,
        String prefix,
        String refreshToken,
        String email,
        String bankName,
        List<String> roles,
        String firstName,
        String lastName,
        Boolean isFirstLogin
){}
