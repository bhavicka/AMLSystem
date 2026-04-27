package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBatchProcessDto {
    private Integer rowNumber;

    @NotBlank(message = "Client number is required")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Client number must be uppercase and numbers only")
    private String clientNumber;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Account number must be uppercase and numbers only")
    private String accountNumber;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Account type must be ALL CAPS with no spaces")
    private String accountType;

    @Pattern(regexp = "^[A-Za-z]*$", message = "Must contain only letters and cannot be empty")
    private String accountStatus;
}
