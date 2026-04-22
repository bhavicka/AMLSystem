package com.tss.AmlSystem.repository;


import com.tss.AmlSystem.entity.tenant.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
