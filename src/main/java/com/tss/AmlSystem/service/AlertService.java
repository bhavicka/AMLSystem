package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.AlertDashboardDto;
import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.models.AlertDetailProjection;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.utils.UniqueNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;
    private final TenantUserRepository tenantUserRepository;

    public AlertDetailDto getAlertDetail(String alertNumber) {
        Long alertId = alertRepository.findAlertIdByAlertNumber(alertNumber).orElseThrow(
                ()->new RuntimeException("Alert not found with alert number: "+alertNumber)
        );
        Authentication authentication=  SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail= authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BANK_ADMIN"));

        AlertDetailProjection projection = alertRepository.findAlertDetail(alertId);
        String assignedUserEmail = projection.getAssignedTo();

        if (!isAdmin) {
            if (assignedUserEmail != null && !assignedUserEmail.equalsIgnoreCase(currentUserEmail)) {
                throw new RuntimeException("You are not authorized to view alerts assigned to another officer.");
            }
        }

        List<Transaction> transactions =
                alertRepository.findTransactionsByAlertId(alertId);

        return alertMapper.toAlertDetailDto(projection,projection.getTotalAmount(), transactions);
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
        alert.setAlertHash(hash);
        alert.setAlertNumber(UniqueNumberGenerator.generateIdentifierNumber("AL"));

        alertRepository.save(alert);
    }
}
