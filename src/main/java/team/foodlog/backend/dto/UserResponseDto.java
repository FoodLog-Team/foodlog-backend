package team.foodlog.backend.dto;

import java.time.Instant;

public record UserResponseDto(
        Long id,
        String email,
        Instant createdAt
) {}
