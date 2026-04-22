package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.request.LoginRequest;
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
import com.tss.AmlSystem.repository.UserRepository;
import com.tss.AmlSystem.security.JwtUtils;
import com.tss.AmlSystem.security.RefreshTokenService;
import com.tss.AmlSystem.security.SecurityUtils;
import com.tss.AmlSystem.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TenantMapper tenantMapper;
    private final TenantUserMapper tenantUserMapper;
    private final UserCredentialMapper userCredentialMapper;

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final UserCredentialRepository userCredentialRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final TenantSchemaService tenantSchemaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);

    @Transactional
    public String registerBank(BankRegisterDto bankRegisterDto){
        Tenant tenant = tenantMapper.toTenant(bankRegisterDto);
        tenant.setSchemaName(tenant.getBankName()+"_schema");

        UserCredential systemUser = userCredentialMapper.toUserCredential(bankRegisterDto);
        systemUser.setRole(GlobalUserRole.BANK_ADMIN);
        systemUser.setTenant(tenant);
        systemUser.setPasswordHash(passwordEncoder.encode(bankRegisterDto.password()));

        TenantUser user = tenantUserMapper.toTenantUser(bankRegisterDto);
        user.setSystemUser(systemUser);
        user.setRole(TenantUserRole.BANK_ADMIN);
        user.setCreatedBy(userCredentialRepository.findById(SecurityUtils.getCurrentUser().orElseThrow().getId()).orElseThrow());

        tenantSchemaService.createSchema(tenant.getSchemaName());
        return "Tenant created";
    }


    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;
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

        return new JwtResponse(
                jwt,
                "Bearer",
                refreshToken,
                userDetails.getEmail(),
                userDetails.getBankName(),
                roles
        );
    }
}
