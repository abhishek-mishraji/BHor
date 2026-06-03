package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /** Revoke all active tokens for a user (used on logout). */
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.userEmail = :email AND r.revoked = false")
    void revokeAllByUserEmail(@Param("email") String email);

    /** Hard-delete expired tokens (can be called by a scheduled cleanup job). */
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    void deleteAllExpiredBefore(@Param("now") LocalDateTime now);
}
