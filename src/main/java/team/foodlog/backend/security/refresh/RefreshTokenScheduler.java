package team.foodlog.backend.security.refresh;

import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.repository.RefreshTokenRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    private final Clock clock;

    @Scheduled(cron = "@weekly")
    @Transactional
    public void cleanupExpiredTokens() {
        int deletedTokensAmount
                = refreshTokenRepository.deleteExpiredTokens(Instant.now(clock));

        log.debug("{} expired refresh tokens were deleted", deletedTokensAmount);
    }

}
