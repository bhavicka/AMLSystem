package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.dto.response.GeneratedAlertDto;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.exception.ResourceNotFoundException;
import com.tss.AmlSystem.exception.UnauthorizedAccessException;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.entity.tenant.Transaction;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.models.RuleContext;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.CustomerRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.utils.UniqueNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;
    private final TenantUserRepository tenantUserRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public AlertDetailDto getAlertDetail(String alertNumber) {
        Alert alert = alertRepository.findAlertByAlertNumber(alertNumber).orElseThrow(
                ()->new ResourceNotFoundException("Alert not found with alert number: "+alertNumber)
        );
        Authentication authentication=  SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail= authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("BANK_ADMIN"));

        if (!isAdmin) {
            if (alert.getCaseId() == null || !alert.getCaseId().getAssignedTo().getEmail().equalsIgnoreCase(currentUserEmail)) {
                throw new UnauthorizedAccessException("You are not authorized to view alerts assigned to another officer.");
            }
        }

        List<Transaction> transactions =alert.getTransactions();

        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AlertDetailDto alertDetailDto = alertMapper.toAlertDetailDto(alert, totalAmount, transactions);
        Customer customer = customerRepository.findByClientNumber(alert.getClientNumber()).orElseThrow(
                ()->new ResourceNotFoundException("Customer not found with client number: "+alert.getClientNumber())
        );
        alertDetailDto.setCustomerFullName(customer.getFirstName()+" "+customer.getLastName());
        return alertDetailDto;
    }

    @Transactional(readOnly = true)
    public Slice<GeneratedAlertDto> getAlertDashboard(AlertStatus alertStatus,String alertNumber,Pageable pageable){

        Authentication authentication=  SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("BANK_ADMIN"));

        if(!isAdmin){
            throw new UnauthorizedAccessException("You are not authorized to view the alert dashboard.");
        }

        return alertRepository.searchAlerts(alertStatus,alertNumber,pageable)
                .map(alertMapper::toGeneratedAlertDto);
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
