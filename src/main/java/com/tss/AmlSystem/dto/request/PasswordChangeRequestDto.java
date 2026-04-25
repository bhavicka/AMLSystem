package com.tss.AmlSystem.dto.request;

public record PasswordChangeRequestDto(
        String email,
        String oldPassword,
        String newPassword
) {
}
