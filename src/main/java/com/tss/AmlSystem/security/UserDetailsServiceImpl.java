package com.tss.AmlSystem.security;

import com.tss.AmlSystem.entity.SystemUser;
import com.tss.AmlSystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SystemUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find user in the public schema
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        // Get tenant details from the linked tenant entity
        String schemaName = user.getTenant().getSchemaName();
        String bankName = user.getTenant().getBankName();

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                schemaName,
                bankName,
                user.getIsActive(),       // from DB
                user.getAccountLocked(),  // from DB
                user.getIsDeleted(),       // from DB
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
