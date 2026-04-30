package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.pdf.StrAlertDto;
import com.tss.AmlSystem.dto.pdf.StrCustomerDto;
import com.tss.AmlSystem.dto.pdf.StrReportDto;
import com.tss.AmlSystem.dto.pdf.StrTransactionDto;
import com.tss.AmlSystem.dto.request.CaseEscalateDto;
import com.tss.AmlSystem.dto.response.AlertDetailDto;
import com.tss.AmlSystem.dto.response.CaseDashboardDto;
import com.tss.AmlSystem.dto.response.CaseDetailDto;
import com.tss.AmlSystem.entity.enums.tenant.AlertStatus;
import com.tss.AmlSystem.entity.enums.tenant.CaseStatus;
import com.tss.AmlSystem.entity.tenant.*;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.mapper.CaseMapper;
import com.tss.AmlSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tss.AmlSystem.utils.UniqueNumberGenerator.generateIdentifierNumber;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final PdfGenerationService pdfGenerationService;

    private final AlertRepository alertRepository;
    private final TenantUserRepository tenantUserRepository;
    private final CaseRepository caseRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

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
            alert.setStatus(AlertStatus.CONVERTED_TO_CASE);
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
                .anyMatch(a->a.getAuthority().equals("BANK_ADMIN"));

        if(!isAdmin && !c.getAssignedTo().getEmail().equalsIgnoreCase(currentUserEmail)){
            throw new RuntimeException("You are not authorized to view cases assigned to another officer.");
        }

        CaseDetailDto dto=caseMapper.toDetailResponseDto(c);
        List<AlertDetailDto> alertDetailDtos = new ArrayList<>();
        for(Alert alert: alerts){
            BigDecimal totalAmount = alert.getTransactions().stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            AlertDetailDto alertDetailDto = alertMapper.toAlertDetailDto(alert,totalAmount, alert.getTransactions());
            alertDetailDtos.add(alertDetailDto);
        }
        dto.setAlerts(alertDetailDtos);

        return dto;
    }

    public CaseDetailDto dismissCase(CaseEscalateDto caseEscalateDto){
        Case case_ = caseRepository.findByCaseReferenceNumber(caseEscalateDto.getCaseReferenceNumber())
                .orElseThrow(() -> new RuntimeException("Case not found with reference number: " + caseEscalateDto.getCaseReferenceNumber()));
        case_.setStatus(CaseStatus.CLOSED);
        case_.setNotes(caseEscalateDto.getNotes());
        case_.setClosedAt(LocalDateTime.now());
        caseRepository.save(case_);
        return caseMapper.toDetailResponseDto(case_);
    }

    public byte[] escalateCase(CaseEscalateDto caseEscalateDto){
        String caseReferenceNumber = caseEscalateDto.getCaseReferenceNumber();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TenantUser tenantUser = tenantUserRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        Case case_ = caseRepository.findByCaseReferenceNumber(caseReferenceNumber)
                .orElseThrow(() -> new RuntimeException("Case not found with reference number: " + caseReferenceNumber));
        if(!case_.getAssignedTo().getEmail().equalsIgnoreCase(email)){
            throw new RuntimeException("You are not authorized to escalate cases assigned to another officer.");
        }
        case_.setStatus(CaseStatus.ESCALATED);
        caseRepository.save(case_);

        TenantUser assignedTo = case_.getAssignedTo();

        StrFilling strFilling = new StrFilling();
        strFilling.setACase(case_);
        strFilling.setFiledBy(assignedTo);
        strFilling.setSupportingNotes(caseEscalateDto.getNotes());
        String strReferenceNumber = generateIdentifierNumber("STR");
        strFilling.setReferenceNumber(strReferenceNumber);

        //report details
        StrReportDto strReportDto = new StrReportDto(
            strReferenceNumber,
            case_.getCaseReferenceNumber(),
            assignedTo.getFirstName() + " " + assignedTo.getLastName(),
            assignedTo.getEmployeeCode(),
            caseEscalateDto.getNotes()
        );

        //alert details
        List<Alert> alertList = alertRepository.findAllByCaseId(case_.getId());
        List<StrAlertDto> strAlertDtoList = new ArrayList<>();
        for(Alert a: alertList){
            StrAlertDto strAlertDto = new StrAlertDto(
                    a.getTenantRule().getRuleCode(),
                    a.getAlertNumber(),
                    a.getCreatedAt().toLocalDate()
            );
            strAlertDtoList.add(strAlertDto);
        }

        Customer customer = customerRepository.findByClientNumber(alertList.get(0).getClientNumber())
                .orElseThrow(() -> new RuntimeException("Customer not found with client number: " + alertList.get(0).getClientNumber()));

        //customer details
        StrCustomerDto strCustomerDto = new StrCustomerDto(
                customer.getClientNumber(),
                customer.getFirstName() + " " + customer.getMiddleName() + " " + customer.getLastName(),
                customer.getAadharNumber(),
                customer.getPan(),
                customer.getOccupation(),
                customer.getRiskRate().toString(),
                Double.parseDouble(customer.getMonthlyIncome().toString()),
                Double.parseDouble(customer.getProfessionMultiplier().toString())
        );

        List<Account> accountList = accountRepository.findByClientNumber(customer.getClientNumber());
        List<StrTransactionDto> strTransactionDtoList = new ArrayList<>();
        for(Account account:accountList){
            List<Transaction> transactionList = transactionRepository.findByAccountNumber(account.getAccountNumber());
            transactionList.addAll(transactionRepository.findByCounterPartyAccountNumber(account.getAccountNumber()));
            for(Transaction transaction: transactionList){
                StrTransactionDto strTransactionDto = new StrTransactionDto(
                        transaction.getTransactionDate(),
                        transaction.getTransactionReferenceNumber(),
                        transaction.getAccountNumber(),
                        transaction.getCounterPartyAccountNumber(),
                        transaction.getTransactionType().toString(),
                        transaction.getTransactionMode().toString(),
                        Double.parseDouble(transaction.getAmount().toString())
                );
                strTransactionDtoList.add(strTransactionDto);
            }
        }
        return pdfGenerationService.generateStrReportPdf(strReportDto, strCustomerDto, strAlertDtoList, strTransactionDtoList);
    }

}