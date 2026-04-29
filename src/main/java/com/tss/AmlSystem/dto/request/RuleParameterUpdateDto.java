package com.tss.AmlSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RuleParameterUpdateDto {
    @NotNull
    Map<String, String> updatedParameters;
}
