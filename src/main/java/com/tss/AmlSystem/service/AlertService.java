package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.AlertDashboardDto;
import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.models.AlertDetailProjection;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.utils.UniqueNumberGenerator;
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

    public void saveAlert(String clientNumber, RuleContext ruleContext,List<Transaction> transactions,String hash){
        boolean exists=alertRepository.existsByAlertHash(hash);

        if(exists)return;

        Alert alert = new Alert();
        alert.setClientNumber(clientNumber);
        alert.setTenantRule(ruleContext.getTenantRule());
        alert.setStatus(AlertStatus.NEW);
        alert.setTransactions(transactions);
        alert.setAlertNumber(UniqueNumberGenerator.generateAlertNumber());

        alertRepository.save(alert);
    }
}
