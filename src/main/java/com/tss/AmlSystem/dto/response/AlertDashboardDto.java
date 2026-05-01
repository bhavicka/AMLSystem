package com.tss.AmlSystem.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AlertDashboardDto {
    private List<GeneratedAlertDto> alerts;
}
