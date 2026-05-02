package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.response.CustomerInfoDto;
import com.tss.AmlSystem.entity.tenant.Customer;
import com.tss.AmlSystem.mapper.CustomerMapper;
import com.tss.AmlSystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    public CustomerInfoDto getCustomerInfo(String customerNumber) {
        log.info("Fetching customer info for customer number: {}", customerNumber);
        Customer customer = customerRepository.findByClientNumber(customerNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toCustomerInfoDto(customer);
    }
}

