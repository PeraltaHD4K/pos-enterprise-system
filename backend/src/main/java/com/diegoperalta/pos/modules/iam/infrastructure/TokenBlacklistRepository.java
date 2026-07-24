package com.diegoperalta.pos.modules.iam.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import com.diegoperalta.pos.modules.iam.domain.TokenBlacklist;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, String> {
}
