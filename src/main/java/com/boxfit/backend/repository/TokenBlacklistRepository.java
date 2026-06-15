package com.boxfit.backend.repository;

import com.boxfit.backend.domain.entity.TokenBlacklist;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
    boolean existsByToken(String token);
}

