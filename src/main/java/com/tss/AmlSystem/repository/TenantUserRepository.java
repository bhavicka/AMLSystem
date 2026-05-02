package com.tss.AmlSystem.repository;

import com.tss.AmlSystem.entity.enums.tenant.TenantUserRole;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
    Optional<TenantUser> findByEmail(String principal);
    Optional<TenantUser> findByEmployeeCode(String employeeCode);
    Optional<TenantUser> findBySystemUser(UserCredential userCredential);
    long count();
    List<TenantUser> findByRole(TenantUserRole role);

}
