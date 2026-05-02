package com.tss.AmlSystem.controller;

import com.tss.AmlSystem.dto.response.CustomerInfoDto;
import com.tss.AmlSystem.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/{customerNumber}")
    @PreAuthorize("hasAuthority('BANK_ADMIN') or hasAuthority('COMPLIANCE_OFFICER')")
    public CustomerInfoDto getCustomerInfo(@PathVariable String customerNumber){
        log.info("Fetching customer info for customer number: {}", customerNumber);
        return customerService.getCustomerInfo(customerNumber);
    }
}
