package com.tss.AmlSystem.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tss.AmlSystem.dto.event.CaseCreatedEvent;
import com.tss.AmlSystem.dto.event.CaseEscalatedEvent;
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
import com.tss.AmlSystem.exception.BusinessValidationException;
import com.tss.AmlSystem.exception.ResourceNotFoundException;
import com.tss.AmlSystem.exception.UnauthorizedAccessException;
import com.tss.AmlSystem.entity.tenant.*;
import com.tss.AmlSystem.mapper.AlertMapper;
import com.tss.AmlSystem.mapper.CaseMapper;
import com.tss.AmlSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tss.AmlSystem.utils.UniqueNumberGenerator.generateIdentifierNumber;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final PdfGenerationService pdfGenerationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final AlertRepository alertRepository;
    private final TenantUserRepository tenantUserRepository;
    private final CaseRepository caseRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final StrFilingRepository strFilingRepository;

    private final CaseMapper caseMapper;
    private final AlertMapper alertMapper;

    private final Cloudinary cloudinary;

    @Transactional
    public void createCase(List<String> alertNumbers,String officerEmail){
        TenantUser officer=tenantUserRepository.findByEmail(officerEmail)
                .orElseThrow(()->new ResourceNotFoundException("User not found with email: "+officerEmail));

        String currentUserEmail=  SecurityContextHolder.getContext().getAuthentication().getName();

        TenantUser bankAdmin=tenantUserRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResourceNotFoundException("User not found with email: "+currentUserEmail));

        Case newCase=new Case();
        newCase.setAssignedTo(officer);
        newCase.setCaseReferenceNumber(generateIdentifierNumber("CASE"));
        newCase.setStatus(CaseStatus.UNDER_INVESTIGATION);
        newCase.setAssignedBy(bankAdmin);

        caseRepository.save(newCase);

        String clientNumber = null;
        for(String alertNumber:alertNumbers){
            Alert alert=alertRepository.findByAlertNumber(alertNumber)
                    .orElseThrow(()->new ResourceNotFoundException("Alert not found with alert number: "+alertNumber));
            if(clientNumber == null){
                clientNumber = alert.getClientNumber();
            }
            if(!clientNumber.equalsIgnoreCase(alert.getClientNumber())){
                throw new BusinessValidationException("Can't select alerts belonging to different customers.");
            }
            alert.setCaseId(newCase);
            alert.setStatus(AlertStatus.CONVERTED_TO_CASE);
            alertRepository.save(alert);
        }

        applicationEventPublisher.publishEvent(new CaseCreatedEvent(officerEmail,officer.getFirstName()+" "+officer.getLastName(), newCase.getCaseReferenceNumber()));
    }

    @Transactional(readOnly = true)
    public Slice<CaseDashboardDto> getAllCases(String requestedEmail,CaseStatus caseStatus,String caseReferenceNumber,Pageable pageable){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail=  authentication.getName();

        boolean isAdmin=authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("BANK_ADMIN"));

        String targetEmailToFilter;
        if (isAdmin) {
            targetEmailToFilter = (requestedEmail != null && !requestedEmail.trim().isEmpty()) ? requestedEmail : null;
        } else {
            targetEmailToFilter = currentUserEmail;
        }
        return caseRepository.searchCases(targetEmailToFilter,caseReferenceNumber,caseStatus,pageable)
                .map(caseMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public CaseDetailDto getCaseDetail(String caseReferenceNumber){
        Case c=caseRepository.findByCaseReferenceNumber(caseReferenceNumber)
                .orElseThrow(()->new ResourceNotFoundException("Case not found with reference number: "+caseReferenceNumber));

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail=  authentication.getName();

        List<Alert> alerts=alertRepository.findAllByCaseId(c.getId());

        boolean isAdmin=authentication.getAuthorities().stream()
                .anyMatch(a->a.getAuthority().equals("BANK_ADMIN"));

        if(!isAdmin && !c.getAssignedTo().getEmail().equalsIgnoreCase(currentUserEmail)){
            throw new UnauthorizedAccessException("You are not authorized to view cases assigned to another officer.");
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

    @Transactional
    public CaseDetailDto dismissCase(CaseEscalateDto caseEscalateDto){
        Case case_ = caseRepository.findByCaseReferenceNumber(caseEscalateDto.getCaseReferenceNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with reference number: " + caseEscalateDto.getCaseReferenceNumber()));
        if (!case_.getStatus().equals(CaseStatus.UNDER_INVESTIGATION)) {
            throw new BusinessValidationException("can only dismiss OPEN case");
        }
        case_.setStatus(CaseStatus.CLOSED);
        case_.setNotes(caseEscalateDto.getNotes());
        case_.setClosedAt(LocalDateTime.now());
        caseRepository.save(case_);

        List<Alert> alerts=alertRepository.findAllByCaseId(case_.getId());

        CaseDetailDto caseDetailDto = caseMapper.toDetailResponseDto(case_);

        List<AlertDetailDto> alertDetailDtos = new ArrayList<>();
        for(Alert alert: alerts){
            BigDecimal totalAmount = alert.getTransactions().stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            AlertDetailDto alertDetailDto = alertMapper.toAlertDetailDto(alert,totalAmount, alert.getTransactions());
            alertDetailDtos.add(alertDetailDto);
        }
        caseDetailDto.setAlerts(alertDetailDtos);
        return caseDetailDto;
    }

    @Transactional
    public String escalateCase(CaseEscalateDto caseEscalateDto){
        String caseReferenceNumber = caseEscalateDto.getCaseReferenceNumber();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TenantUser tenantUser = tenantUserRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        Case case_ = caseRepository.findByCaseReferenceNumber(caseReferenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found with reference number: " + caseReferenceNumber));
        if(!case_.getAssignedTo().getEmail().equalsIgnoreCase(email)){
            throw new UnauthorizedAccessException("You are not authorized to escalate cases assigned to another officer.");
        }
        if (!case_.getStatus().equals(CaseStatus.UNDER_INVESTIGATION)) {
            throw new BusinessValidationException("can only escalate OPEN case");
        }
        case_.setStatus(CaseStatus.ESCALATED);
        caseRepository.save(case_);

        TenantUser assignedTo = case_.getAssignedTo();
        TenantUser assignedBy=case_.getAssignedBy();

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
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with client number: " + alertList.get(0).getClientNumber()));

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

        List<Transaction> transactionList = alertRepository.findAllByCaseId(case_.getId()).stream()
                .flatMap(alert -> alert.getTransactions().stream())
                .toList();
        List<StrTransactionDto> strTransactionDtoList = new ArrayList<>();
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

        byte[] pdfBytes = pdfGenerationService.generateStrReportPdf(strReportDto, strCustomerDto, strAlertDtoList, strTransactionDtoList);


        try {
            String fileName = "str_report_" + case_.getCaseReferenceNumber() + "_" + System.currentTimeMillis() + ".pdf";

            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "resource_type", "raw",
                    "public_id", fileName
            );

            Map uploadResult = cloudinary.uploader().upload(pdfBytes, uploadOptions);

            String pdfUrl = (String) uploadResult.get("secure_url");
            strFilling.setPdfStoragePath(pdfUrl);
            strFilingRepository.save(strFilling);

            applicationEventPublisher.publishEvent(new CaseEscalatedEvent(
                    assignedBy.getEmail(), assignedBy.getFirstName()+" "+assignedBy.getLastName(), case_.getCaseReferenceNumber()
            ));
            
            return pdfUrl;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading PDF to Cloudinary", e);
        }


    }
}