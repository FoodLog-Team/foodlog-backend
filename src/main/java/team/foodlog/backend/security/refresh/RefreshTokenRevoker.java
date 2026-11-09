package team.foodlog.backend.security.refresh;

import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.repository.RefreshTokenRepository;

@Component
@RequiredArgsConstructor
public class RefreshTokenRevoker {

    private final RefreshTokenRepository refreshTokenRepository;

    private final Clock clock;

    @Transactional
    void revokeAll(Long userId) {
        refreshTokenRepository.findAllNotRevokedTokensByUserId(userId)
                .forEach(refreshToken ->
                    refreshToken.setRevokedAt(Instant.now(clock)));
    }

}
