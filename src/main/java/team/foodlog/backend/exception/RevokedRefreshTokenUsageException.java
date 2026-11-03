package team.foodlog.backend.exception;

import lombok.Getter;

@Getter
public class RevokedRefreshTokenUsageException extends RuntimeException {

    private final Long refreshTokenId;

    private final Long userId;

    public RevokedRefreshTokenUsageException(String message, Long refreshTokenId, Long userId) {
        super(message);
        this.refreshTokenId = refreshTokenId;
        this.userId = userId;
    }
}
