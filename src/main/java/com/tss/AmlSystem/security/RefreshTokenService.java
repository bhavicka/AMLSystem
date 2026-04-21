package com.tss.AmlSystem.security;

import com.tss.AmlSystem.entity.SystemUser;
import com.tss.AmlSystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final SystemUserRepository userRepository;

    public Optional<SystemUser> findByToken(String token) {
        return userRepository.findByRefreshToken(token);
    }

    @Transactional
    public String createRefreshToken(Long userId) {
        SystemUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String token = UUID.randomUUID().toString();

        user.setRefreshToken(token);
        user.setRefreshTokenExpiry(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenDurationMs)));
        userRepository.save(user);

        return token;
    }

    public SystemUser verifyExpiration(SystemUser user) {
        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            // Token has expired - clear it from DB
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }
        return user;
    }
}
