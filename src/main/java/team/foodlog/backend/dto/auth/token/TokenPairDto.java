package team.foodlog.backend.dto.auth.token;

public record TokenPairDto(
        String accessToken,
        String refreshToken
) {}
