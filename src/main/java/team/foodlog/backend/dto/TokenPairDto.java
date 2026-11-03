package team.foodlog.backend.dto;

public record TokenPairDto(
        String accessToken,
        String refreshToken
) {}
