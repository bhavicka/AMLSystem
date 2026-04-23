package com.tss.AmlSystem.dto.request;

import com.tss.AmlSystem.entity.enums.Severity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AlertFilterRequestDto {
    private String ruleName;
    private Severity severity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int page = 0;
    private int size = 10;
}
