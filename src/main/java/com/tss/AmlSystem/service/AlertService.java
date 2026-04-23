package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.AlertDashboardDto;
import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.models.AlertDetailProjection;
import com.tss.AmlSystem.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    public AlertDetailDto getAlertDetail(Long alertId) {

        AlertDetailProjection projection = alertRepository.findAlertDetail(alertId);
        BigDecimal totalAmount = projection.getTotalAmount() != null
                ? projection.getTotalAmount()
                : BigDecimal.ZERO;

        List<Transaction> transactions =
                alertRepository.findTransactionsByAlertId(alertId);

        return alertMapper.toAlertDetailDto(projection,totalAmount, transactions);
    }

    public AlertDashboardDto getAlertDashboard(){
        AlertDashboardDto alertDashboardDto=new AlertDashboardDto();

        alertDashboardDto.setAlerts(alertMapper.toGeneratedAlertDtos(alertRepository.findAll()));

        return alertDashboardDto;
    }
}
