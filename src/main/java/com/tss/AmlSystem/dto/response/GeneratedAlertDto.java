package com.tss.AmlSystem.dto.response;

import com.tss.AmlSystem.entity.enums.Severity;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GeneratedAlertDto {
    private String alertNumber;
    private AlertStatus status;
    private String brokenRuleName;
    private Severity severity;
    private LocalDateTime generatedAt;
}
