package team.foodlog.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.RefreshTokenDto;
import team.foodlog.backend.exception.RefreshTokenExpiredException;
import team.foodlog.backend.exception.RefreshTokenNotFoundException;
import team.foodlog.backend.exception.RevokedRefreshTokenUsageException;
import team.foodlog.backend.model.RefreshToken;
import team.foodlog.backend.properties.RefreshTokenProperties;
import team.foodlog.backend.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
public class Sha256RefreshTokenService implements RefreshTokenService<String, RefreshTokenDto> {

    private static final String ALGORITHM = "SHA-256";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    private final RefreshTokenProperties refreshTokenProperties;

    private final RefreshTokenRevoker refreshTokenRevoker;

    private final RefreshTokenLock refreshTokenLock;

    private final Clock clock;

    @Override
    @Transactional
    public RefreshTokenDto create(Long userId) {
        refreshTokenLock.lock(userId);

        Instant now = Instant.now(clock);

        boolean isLimitExceeded = refreshTokenRepository.countActiveTokensByUserId(userId, now)
                    >= refreshTokenProperties.maxAmount();

        if (isLimitExceeded) {
            refreshTokenRepository.revokeOldestActiveToken(userId, now);
        }

        return new RefreshTokenDto(
                userId,
                this.create0(userId)
        );
    }

    @Override
    @Transactional
    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByHashForUpdate(hashOpaqueToken(token))
                .orElseThrow(() -> new RefreshTokenNotFoundException("Nonexisting refresh token"));
        this.revoke0(refreshToken);
    }

    @Override
    @Transactional
    public RefreshTokenDto rotate(String token) {
        Long userId = refreshTokenRepository.findUserIdByHash(hashOpaqueToken(token))
                .orElseThrow(
                        () -> new RefreshTokenNotFoundException("Nonexisting refresh token")
                );

        return new RefreshTokenDto(
                userId,
                this.rotate0(token, userId)
        );
    }

    @Override
    @Transactional
    public void revokeAll(Long userId) {
        refreshTokenLock.lock(userId);
        refreshTokenRevoker.revokeAll(userId);
    }

    private String create0(Long userId) {
        String opaqueToken = generateOpaqueToken();

        System.out.println("5984 " + refreshTokenProperties.maxAmount());
        System.out.println("5984 " + refreshTokenProperties.expiration());
        System.out.println("5984 " + refreshTokenProperties.cookies().path());
        System.out.println("5984 " + refreshTokenProperties.cookies().sameSite());
        System.out.println("5984 " + refreshTokenProperties.cookies().maxAge());

        RefreshToken refreshToken = new RefreshToken()
                .setUserId(userId)
                .setHash(hashOpaqueToken(opaqueToken))
                .setExpiresAt(
                    Instant.now(clock)
                        .plus(refreshTokenProperties.expiration())
            );

        refreshTokenRepository.save(refreshToken);

        return opaqueToken;
    }

    private void validate0(RefreshToken refreshToken) {
        if (refreshToken.getRevokedAt() != null) {
            throw new RevokedRefreshTokenUsageException(
                    "Refresh token is revoked. Can be a hacker attack",
                    refreshToken.getId(),
                    refreshToken.getUserId()
            );
        }

        if (!(Instant.now(clock).isBefore(refreshToken.getExpiresAt()))) {
            throw new RefreshTokenExpiredException(
                "Refresh token is expired and cannot be used anymore");
        }
    }

    private void revoke0(RefreshToken refreshToken) {
        this.validate0(refreshToken);
        refreshToken.setRevokedAt(Instant.now(clock));
    }

    private String rotate0(String token, Long userId) {
        refreshTokenLock.lock(userId);

        String hash = hashOpaqueToken(token);
        RefreshToken refreshToken = refreshTokenRepository
                .findByHashForUpdate(hash)
                .orElseThrow(
                        () -> new RefreshTokenNotFoundException("Nonexisting refresh token")
                );

        try {
            this.revoke0(refreshToken);
            return this.create0(userId);
        } catch (RevokedRefreshTokenUsageException e) {
            refreshTokenRevoker.revokeAll(userId);
            throw e;
        }
    }

    private String generateOpaqueToken() {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);

        return HexFormat.of().formatHex(randomBytes);
    }

    private String hashOpaqueToken(String opaqueToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(opaqueToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Invalid hashing algorithm was provided");
        }
    }
}
