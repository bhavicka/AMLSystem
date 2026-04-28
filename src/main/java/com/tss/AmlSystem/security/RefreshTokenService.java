package com.tss.AmlSystem.security;

import com.tss.AmlSystem.entity.master.UserCredential;
import com.tss.AmlSystem.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import com.tss.AmlSystem.entity.enums.LogTag;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final UserCredentialRepository userCredentialRepository;

    public Optional<UserCredential> findByToken(String token) {
        return userCredentialRepository.findByRefreshToken(token);
    }

    @Transactional
    public String createRefreshToken(Long userId) {
        log.info("{} Creating refresh token for user ID: {}", LogTag.AUTH.getValue(), userId);
        UserCredential user = userCredentialRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String token = UUID.randomUUID().toString();

        user.setRefreshToken(token);
        user.setRefreshTokenExpiry(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenDurationMs)));
        userCredentialRepository.save(user);

        return token;
    }

    public UserCredential verifyExpiration(UserCredential user) {
        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("{} {} Refresh token expired for user: {}", LogTag.AUTH.getValue(), LogTag.SECURITY.getValue(), user.getEmail());
            // Token has expired - clear it from DB
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userCredentialRepository.save(user);
            throw new RuntimeException("Refresh token was expired. Please make a new login request");
        }
        return user;
    }
}
