package com.tss.AmlSystem.service;

import com.tss.AmlSystem.dto.request.BankRegisterDto;
import com.tss.AmlSystem.dto.request.LoginRequest;
import com.tss.AmlSystem.dto.response.JwtResponse;
import com.tss.AmlSystem.entity.SystemUser;
import com.tss.AmlSystem.entity.Tenant;
import com.tss.AmlSystem.entity.User;
import com.tss.AmlSystem.entity.enums.SystemUserRole;
import com.tss.AmlSystem.entity.enums.TenantUserRole;
import com.tss.AmlSystem.mapper.SystemUserMapper;
import com.tss.AmlSystem.mapper.TenantMapper;
import com.tss.AmlSystem.mapper.UserMapper;
import com.tss.AmlSystem.repository.SystemUserRepository;
import com.tss.AmlSystem.repository.TenantRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;
    private final SystemUserMapper systemUserMapper;

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SystemUserRepository systemUserRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final TenantSchemaService tenantSchemaService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);

    @Transactional
    public String registerBank(BankRegisterDto bankRegisterDto){
        Tenant tenant = tenantMapper.toTenant(bankRegisterDto);
        tenant.setSchemaName(tenant.getBankName()+"_schema");

        SystemUser systemUser = systemUserMapper.toSystemUser(bankRegisterDto);
        systemUser.setRole(SystemUserRole.BANK_ADMIN);
        systemUser.setTenant(tenant);
        systemUser.setPasswordHash(passwordEncoder.encode(bankRegisterDto.password()));

        User user = userMapper.toUser(bankRegisterDto);
        user.setSystemUser(systemUser);
        user.setUserRole(TenantUserRole.BANK_ADMIN);
        user.setCreatedBy(systemUserRepository.findById(SecurityUtils.getCurrentUser().orElseThrow().getId()).orElseThrow());

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
        SystemUser user = systemUserRepository.findById(userDetails.getId())
                .orElseThrow();

        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);

        systemUserRepository.save(user);

        String jwt = jwtUtils.generateJwtToken(
                userDetails.getEmail(),
                userDetails.getBankName(),
                userDetails.getSchemaName()
        );

        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList().getFirst();

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
