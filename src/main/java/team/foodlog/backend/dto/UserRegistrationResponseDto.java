package team.foodlog.backend.dto;

import java.time.Instant;

public record UserRegistrationResponseDto(
        String token,
        Long id,
        String email,
        Instant createdAt
) {}
