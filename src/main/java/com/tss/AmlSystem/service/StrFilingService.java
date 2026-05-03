package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.StrFilingDetailDto;
import com.tss.AmlSystem.dto.response.StrFilingInlineDto;
import com.tss.AmlSystem.entity.enums.tenant.TenantUserRole;
import com.tss.AmlSystem.exception.ResourceNotFoundException;
import com.tss.AmlSystem.exception.UnauthorizedAccessException;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.entity.tenant.StrFilling;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.mapper.StrFilingMapper;
import com.tss.AmlSystem.repository.AlertRepository;
import com.tss.AmlSystem.repository.CustomerRepository;
import com.tss.AmlSystem.repository.StrFilingRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StrFilingService {
    private final StrFilingRepository strFilingRepository;
    private final TenantUserRepository tenantUserRepository;

    private final StrFilingMapper strFilingMapper;
    private final CustomerRepository customerRepository;
    private final AlertRepository alertRepository;

    @Transactional(readOnly = true)
    public Slice<StrFilingInlineDto> getAllStrFilings(Pageable pageable){
        log.info("Fetching STR filings with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TenantUser tenantUser = tenantUserRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Current user not found in database")
        );

        Slice<StrFilling> strFillingList;
        if(tenantUser.getRole().equals(TenantUserRole.BANK_ADMIN)) {
            strFillingList = strFilingRepository.findAll(pageable);
            log.info("Total STR filings fetched for BANK_ADMIN: {}", strFillingList.getSize());
        }
        else {
            strFillingList = strFilingRepository.findByFiledBy(pageable, tenantUser);
        }
        return strFillingList
                .map(strFiling -> {
                    TenantUser filedBy = strFiling.getFiledBy();
                    String fullName = filedBy.getFirstName() + " " + filedBy.getLastName();
                    return new StrFilingInlineDto(
                            strFiling.getACase().getCaseReferenceNumber(),
                            strFiling.getReferenceNumber(),
                            fullName
                    );
                });
    }

    @Transactional(readOnly = true)
    public StrFilingDetailDto getStrFilingDetails(String referenceNumber){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        TenantUser tenantUser = tenantUserRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Current user not found in database")
        );
        StrFilling strFiling = strFilingRepository.findByReferenceNumber(referenceNumber).orElseThrow(
                () -> new ResourceNotFoundException("STR filing not found with reference number: "+referenceNumber)
        );
        Customer customer = customerRepository.findByClientNumber(
                alertRepository.findAllByCaseId(strFiling.getACase().getId()).get(0).getClientNumber()
        ).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found for STR filing with reference number: "+referenceNumber)
        );

        StrFilingDetailDto strFilingDetailDto = strFilingMapper.toStrFilingDetailDto(strFiling, customer.getClientNumber());

        if(tenantUser.getRole().equals(TenantUserRole.BANK_ADMIN)){
            return strFilingDetailDto;
        }
        else {
            if(tenantUser.getEmail().equalsIgnoreCase(strFiling.getFiledBy().getEmail())){
                return strFilingDetailDto;
            }
            else {
                throw new UnauthorizedAccessException("You are not authorized to view details of STR filings filed by another officer.");
            }
        }
    }

}
