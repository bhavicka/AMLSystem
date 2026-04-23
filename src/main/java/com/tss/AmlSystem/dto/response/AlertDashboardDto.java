package com.tss.AmlSystem.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AlertDashboardDto {
    private List<GeneratedAlertDto> alerts;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    // filters applied (optional but useful)
    private String ruleName;
    private String severity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
