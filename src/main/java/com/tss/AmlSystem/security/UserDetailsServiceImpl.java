package com.tss.AmlSystem.security;

import com.tss.AmlSystem.entity.enums.master.GlobalUserRole;
import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import com.tss.AmlSystem.entity.enums.LogTag;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("{} Loading user details for email: {}", LogTag.AUTH.getValue(), email);
        // Find user in the public schema
        UserCredential user = userCredentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));


        // Get tenant details from the linked tenant entity
        String schemaName;
        String bankName;
        if(user.getRole().equals(GlobalUserRole.SYSTEM_ADMIN)) {
            schemaName = "public";
            bankName = "";
        }
        else {
            schemaName = user.getTenant().getSchemaName();
            bankName = user.getTenant().getBankName();
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                schemaName,
                bankName,
                List.of(user.getRole().toString()),
                user.getIsActive(),       // from DB
                user.getAccountLocked(),  // from DB
                user.getIsDeleted(),       // from DB
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
