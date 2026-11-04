package team.foodlog.backend.repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team.foodlog.backend.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query(value = """
            SELECT COUNT(rt.id) FROM RefreshToken rt
            WHERE rt.userId = :userId
            AND rt.revokedAt IS NULL
            AND rt.expiresAt > :now
            """)
    long countActiveTokensByUserId(Long userId, Instant now);

    @Query(value = """
            SELECT rt FROM RefreshToken rt
            WHERE rt.userId = :userId
            AND rt.revokedAt IS NULL
            """)
    List<RefreshToken> findAllNotRevokedTokensByUserId(Long userId);

    @Modifying
    @Query(value = """
            WITH oldest_token AS (
                SELECT id
                FROM refresh_tokens
                WHERE user_id = :userId
                    AND revoked_at IS NULL
                    AND expires_at > :now
                ORDER BY created_at ASC, id ASC
                LIMIT 1
                FOR UPDATE
            )
            UPDATE refresh_tokens
            SET revoked_at = :now
            FROM oldest_token
            WHERE refresh_tokens.id = oldest_token.id
            """, nativeQuery = true)
    void revokeOldestActiveToken(Long userId, Instant now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT rt FROM RefreshToken rt
            WHERE rt.tokenHash = :hash
            """)
    Optional<RefreshToken> findByHashForUpdate(String hash);

    @Query("""
            SELECT rt.userId FROM RefreshToken rt
            WHERE rt.tokenHash = :hash
            """)
    Optional<Long> findUserIdByHash(String hash);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken rt
            WHERE rt.expiresAt <= :now
            """)
    int deleteExpiredTokens(Instant now);

}
