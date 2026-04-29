package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;
import com.tss.AmlSystem.entity.tenant.Alert;
import com.tss.AmlSystem.entity.tenant.Case;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.mapper.CaseMapper;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.CaseRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.tss.AmlSystem.utils.UniqueNumberGenerator.generateIdentifierNumber;

@Service
@RequiredArgsConstructor
public class CaseService {
    private final AlertRepository alertRepository;
    private final TenantUserRepository tenantUserRepository;
    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;
    private final AlertMapper alertMapper;

    @Transactional
    public void createCase(List<String> alertNumbers,String officerEmail){
        TenantUser officer=tenantUserRepository.findByEmail(officerEmail)
                .orElseThrow(()->new RuntimeException("User not found with email: "+officerEmail));

        String currentUserEmail=  SecurityContextHolder.getContext().getAuthentication().getName();

        TenantUser bankAdmin=tenantUserRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new RuntimeException("User not found with email: "+currentUserEmail));

        Case newCase=new Case();
        newCase.setAssignedTo(officer);
        newCase.setCaseReferenceNumber(generateIdentifierNumber("CASE"));
        newCase.setStatus(CaseStatus.OPEN);
        newCase.setAssignedBy(bankAdmin);

        caseRepository.save(newCase);

        for(String alertNumber:alertNumbers){
            Alert alert=alertRepository.findByAlertNumber(alertNumber)
                    .orElseThrow(()->new RuntimeException("Alert not found with alert number: "+alertNumber));
            alert.setCaseId(newCase);
            alertRepository.save(alert);

        }

    }

    @Transactional(readOnly = true)
    public List<CaseDashboardDto> getAllCases(){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail=  authentication.getName();

        boolean isAdmin=authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_BANK_ADMIN"));

        List<Case> cases;
        if(isAdmin){
            cases=caseRepository.findAll();
        }
        else{
            cases=caseRepository.findAllByAssignedToEmail(currentUserEmail);
        }
        return caseMapper.toResponseDtoList(cases);
    }

    public CaseDetailDto getCaseDetail(String caseReferenceNumber){
        Case c=caseRepository.findByCaseReferenceNumber(caseReferenceNumber)
                .orElseThrow(()->new RuntimeException("Case not found with reference number: "+caseReferenceNumber));

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail=  authentication.getName();

        List<Alert> alerts=alertRepository.findAllByCaseId(c.getId());

        boolean isAdmin=authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("ROLE_BANK_ADMIN"));

        if(!isAdmin && !c.getAssignedTo().getEmail().equalsIgnoreCase(currentUserEmail)){
            throw new RuntimeException("You are not authorized to view cases assigned to another officer.");
        }

        CaseDetailDto dto=caseMapper.toDetailResponseDto(c);
        dto.setAlerts(alertMapper.toGeneratedAlertDtos(alerts));

        return dto;
    }
}
