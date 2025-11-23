// src/main/java/com/example/everydayweft/service/RefreshTokenService.java
package com.example.everydayweft.service;

import com.example.everydayweft.model.RefreshToken;
import com.example.everydayweft.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long refreshDurationSeconds = 60L * 60 * 24 * 30; // 30 days

    public RefreshTokenService(RefreshTokenRepository repo) { this.repo = repo; }

    public RefreshToken createTokenForUser(Long userId) {
        byte[] random = new byte[64];
        secureRandom.nextBytes(random);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(random);

        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setUserId(userId);
        rt.setExpiryDate(Instant.now().plusSeconds(refreshDurationSeconds));
        return repo.save(rt);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repo.findByToken(token);
    }

    public void deleteByUserId(Long userId) {
        repo.deleteByUserId(userId);
    }

    public void delete(RefreshToken token) {
        repo.delete(token);
    }
}
