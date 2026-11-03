package team.foodlog.backend.dto;

public record RefreshTokenDto(
        Long userId,
        String refreshToken
) {}
