package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.event.UserRegisteredEvent;
import com.tss.AmlSystem.dto.request.*;
import com.tss.AmlSystem.dto.response.ComplianceOfficerRegisteredDto;
import com.tss.AmlSystem.dto.response.JwtResponse;
import com.tss.AmlSystem.entity.enums.master.GlobalUserRole;
import com.tss.AmlSystem.entity.enums.tenant.TenantUserRole;
import com.tss.AmlSystem.entity.master.Tenant;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.entity.tenant.TenantUser;
import com.tss.AmlSystem.mapper.TenantMapper;
import com.tss.AmlSystem.mapper.TenantUserMapper;
import com.tss.AmlSystem.mapper.UserCredentialMapper;
import com.tss.AmlSystem.repository.TenantRepository;
import com.tss.AmlSystem.repository.UserCredentialRepository;
import com.tss.AmlSystem.repository.TenantUserRepository;
import com.tss.AmlSystem.security.JwtUtils;
import com.tss.AmlSystem.security.RefreshTokenService;
import com.tss.AmlSystem.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TenantMapper tenantMapper;
    private final TenantUserMapper tenantUserMapper;
    private final UserCredentialMapper userCredentialMapper;

    private final TenantRepository tenantRepository;
    private final TenantUserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final TenantSchemaService tenantSchemaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);
    private final ApplicationEventPublisher eventPublisher;

    public String registerBank(BankRegisterDto bankRegisterDto){
        Tenant tenant = tenantMapper.toTenant(bankRegisterDto);
        String schemaName = tenant.getBankName().replaceAll("\\s+", "_").toLowerCase() + "_schema";
        tenant.setSchemaName(schemaName);
        tenantRepository.save(tenant);

        String password = generateSecurePassword();
        UserCredential userCredential = userCredentialMapper.toUserCredential(bankRegisterDto);
        userCredential.setRole(GlobalUserRole.BANK_ADMIN);
        userCredential.setTenant(tenant);
        userCredential.setPasswordHash(passwordEncoder.encode(password));
        userCredentialRepository.save(userCredential);

        tenantSchemaService.createSchema(tenant.getSchemaName());
        TenantContext.setCurrentTenant(tenant.getSchemaName());
        try{
            tenantSchemaService.populateTenantSchema(bankRegisterDto, userCredential, tenant.getSchemaName());
        }finally {
            TenantContext.clear();
        }
        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        userCredential.getEmail(),
                        bankRegisterDto.firstName() + " " + bankRegisterDto.lastName(),
                        password
                )
        );
        return "Tenant created";
    }

    public ComplianceOfficerRegisteredDto registerComplianceOfficer(ComplianceOfficerRegisterDto complianceOfficerRegisterDto) {
        String currentTenant = TenantContext.getCurrentTenant();
        if(currentTenant == null) {
            throw new RuntimeException("No tenant context found");
        }
        String password = generateSecurePassword();
        UserCredential userCredential = userCredentialMapper.toUserCredential(complianceOfficerRegisterDto);
        userCredential.setRole(GlobalUserRole.COMPLIANCE_OFFICER);
        userCredential.setPasswordHash(passwordEncoder.encode(password));
        userCredential.setTenant(tenantRepository.findBySchemaName(currentTenant)
                .orElseThrow(() -> new RuntimeException("Tenant not found for schema: " + currentTenant)));
        userCredentialRepository.save(userCredential);

        TenantUser tenantUser = tenantUserMapper.toTenantUser(complianceOfficerRegisterDto);
        tenantUser.setRole(TenantUserRole.COMPLIANCE_OFFICER);
        tenantUser.setSystemUser(userCredential);
        userRepository.save(tenantUser);
        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        userCredential.getEmail(),
                        complianceOfficerRegisterDto.firstName() + " " + complianceOfficerRegisterDto.lastName(),
                        password
                )
        );
        return new ComplianceOfficerRegisteredDto(
                userCredential.getEmail(),
                tenantUser.getEmployeeCode(),
                tenantUser.getRole().toString()
        );
    }

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        System.out.println("yahan to aa //");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        System.out.println("yahan to aa");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;
        System.out.println(userDetails.getEmail());
        UserCredential user = userCredentialRepository.findById(userDetails.getId())
                .orElseThrow();
        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        userCredentialRepository.save(user);
        String jwt = jwtUtils.generateJwtToken(
                userDetails.getEmail(),
                userDetails.getBankName(),
                userDetails.getSchemaName(),
                userDetails.getRoles()
        );
        System.out.println("yahan aaya");
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        if(!roles.get(0).equals(GlobalUserRole.SYSTEM_ADMIN.toString())) {
            String schemaName = userDetails.getSchemaName();
            TenantContext.setCurrentTenant(schemaName);
        }
        return new JwtResponse(
                jwt,
                "Bearer",
                refreshToken,
                userDetails.getEmail(),
                userDetails.getBankName(),
                roles
        );
    }
    @Transactional
    public Boolean updatePassword(PasswordChangeRequestDto passwordChangeRequestDto){
        UserCredential userCredential = userCredentialRepository.findByEmail(passwordChangeRequestDto.email())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + passwordChangeRequestDto.email()));
        if(userCredential.getIsFirstLogin()){
            userCredential.setPasswordHash(passwordEncoder.encode(passwordChangeRequestDto.newPassword()));
            userCredential.setIsFirstLogin(false);
        }
        return true;
    }
    public JwtResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(user -> {
                    // Generate new JWT
                    String token = jwtUtils.generateJwtToken(
                            user.getEmail(),
                            user.getTenant() != null ? user.getTenant().getBankName() : "SYSTEM",
                            user.getTenant() != null ? user.getTenant().getSchemaName() : "public",
                            List.of(user.getRole().name())
                    );

                    // Return response with new JWT and existing/new refresh token
                    return new JwtResponse(
                            token,
                            "Bearer",
                            user.getRefreshToken(),
                            user.getEmail(),
                            user.getTenant() != null ? user.getTenant().getBankName() : "SYSTEM",
                            List.of(user.getRole().name())
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
    private String generateSecurePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 12)
                .map(i -> chars.charAt(random.nextInt(chars.length())))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
