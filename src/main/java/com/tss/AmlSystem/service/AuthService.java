package com.tss.AmlSystem.service;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import com.tss.AmlSystem.dto.event.UserRegisteredEvent;
import com.tss.AmlSystem.dto.request.*;
import com.tss.AmlSystem.dto.response.ComplianceOfficerRegisteredDto;
import com.tss.AmlSystem.dto.response.LoginResponseDto;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.tss.AmlSystem.entity.enums.LogTag;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final TenantUserRepository tenantUserRepository;

    public String registerBank(BankRegisterDto bankRegisterDto){
        log.info("{} Attempting to register new bank: {}", LogTag.TENANT.getValue(), bankRegisterDto.bankName());
        userCredentialRepository.findByEmail(bankRegisterDto.bankAdminEmail()).ifPresent(user -> {
            log.warn("{} {} Email already in use during bank registration: {}", LogTag.TENANT.getValue(), LogTag.SECURITY.getValue(), bankRegisterDto.bankAdminEmail());
            throw new RuntimeException("Email is already in use: " + bankRegisterDto.bankAdminEmail());
        });
        tenantRepository.findByContactEmail(bankRegisterDto.contactEmail()).ifPresent(tenant -> {
            log.warn("{} {} Contact email already associated with another tenant during bank registration: {}", LogTag.TENANT.getValue(), LogTag.SECURITY.getValue(), bankRegisterDto.contactEmail());
            throw new RuntimeException("Contact email is already associated with another tenant: " + bankRegisterDto.contactEmail());
        });
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
            tenantSchemaService.populateTenantSchema(bankRegisterDto, userCredential);
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
        log.info("{} Bank registered successfully with schema: {}", LogTag.TENANT.getValue(), schemaName);
        return "Tenant created";
    }

    public ComplianceOfficerRegisteredDto registerComplianceOfficer(ComplianceOfficerRegisterDto complianceOfficerRegisterDto) {
        String currentTenant = TenantContext.getCurrentTenant();
        log.info("{} Attempting to register Compliance Officer for tenant schema: {}", LogTag.TENANT.getValue(), currentTenant);
        if(currentTenant == null) {
            log.error("{} {} Missing tenant context during compliance officer registration.", LogTag.TENANT.getValue(), LogTag.SECURITY.getValue());
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
        log.info("{} Compliance Officer registered successfully: {}", LogTag.TENANT.getValue(), userCredential.getEmail());
        return new ComplianceOfficerRegisteredDto(
                userCredential.getEmail(),
                tenantUser.getEmployeeCode(),
                tenantUser.getRole().toString()
        );
    }

    public LoginResponseDto login(LoginRequest loginRequest) {
        log.info("{} Login attempt for user: {}", LogTag.AUTH.getValue(), loginRequest.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;
        log.info("{} Successful authentication for: {}", LogTag.AUTH.getValue(), userDetails.getEmail());
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
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        TenantUser tenantUser = null;
        if(!roles.get(0).equals(GlobalUserRole.SYSTEM_ADMIN.toString())) {
            String schemaName = userDetails.getSchemaName();
            TenantContext.clear();
            TenantContext.setCurrentTenant(schemaName);
            tenantUser = tenantUserRepository.findByEmail(userDetails.getEmail()).orElseThrow();
        }
        return new LoginResponseDto(
                jwt,
                "Bearer",
                refreshToken,
                userDetails.getEmail(),
                userDetails.getBankName(),
                roles,
                (tenantUser==null)? "System ": tenantUser.getFirstName(),
                (tenantUser==null)? "Admin": tenantUser.getLastName(),
                (tenantUser==null)? Boolean.FALSE: user.getIsFirstLogin()
        );
    }
    @Transactional
    public Boolean updatePassword(PasswordChangeRequestDto passwordChangeRequestDto){
        log.info("{} Password update requested for: {}", LogTag.AUTH.getValue(), passwordChangeRequestDto.email());
        UserCredential userCredential = userCredentialRepository.findByEmail(passwordChangeRequestDto.email())
                .orElseThrow(() -> {
                    log.warn("{} User not found for password update: {}", LogTag.AUTH.getValue(), passwordChangeRequestDto.email());
                    return new RuntimeException("User not found with email: " + passwordChangeRequestDto.email());
                });
        if(userCredential.getIsFirstLogin()){
            userCredential.setPasswordHash(passwordEncoder.encode(passwordChangeRequestDto.newPassword()));
            userCredential.setIsFirstLogin(false);
            log.info("{} Password successfully updated for: {}", LogTag.AUTH.getValue(), passwordChangeRequestDto.email());
        }
        return true;
    }
    public LoginResponseDto refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();
        log.info("{} Refresh token process started", LogTag.AUTH.getValue());

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

                    log.info("{} Refresh token successful for: {}", LogTag.AUTH.getValue(), user.getEmail());
                    TenantUser tenantUser = tenantUserRepository.findByEmail(user.getEmail()).orElseThrow();
                    return new LoginResponseDto(
                            token,
                            "Bearer",
                            user.getRefreshToken(),
                            user.getEmail(),
                            user.getTenant() != null ? user.getTenant().getBankName() : "SYSTEM",
                            List.of(user.getRole().name()),
                            tenantUser.getFirstName(),
                            tenantUser.getLastName(),
                            tenantUser.getSystemUser().getIsFirstLogin()
                    );
                })
                .orElseThrow(() -> {
                    log.error("{} {} Invalid or missing refresh token", LogTag.AUTH.getValue(), LogTag.SECURITY.getValue());
                    return new RuntimeException("Refresh token is not in database!");
                });
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
