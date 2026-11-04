package team.foodlog.backend.dto.auth.token;

public record RefreshTokenDto(
        Long userId,
        String refreshToken
) {}
