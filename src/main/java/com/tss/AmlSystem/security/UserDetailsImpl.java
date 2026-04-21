package com.tss.AmlSystem.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private String schemaName;
    private String bankName;
    private boolean active;
    private boolean locked;
    private boolean deleted;

    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You don't have an 'account_expiry' column yet, so true is fine.
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked; // Maps to your 'account_locked' column
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Usually used for password rotation policies
    }

    @Override
    public boolean isEnabled() {
        // User is enabled only if active is true AND deleted is false
        return active && !deleted;
    }
}