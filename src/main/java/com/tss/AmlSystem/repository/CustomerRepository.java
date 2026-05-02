package com.tss.AmlSystem.repository;


import com.tss.AmlSystem.entity.tenant.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    boolean existsByClientNumber(String s);
    Optional<Customer> findByClientNumber(@NotBlank @Size(max = 255) String clientNumber);
}
