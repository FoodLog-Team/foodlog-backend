package team.foodlog.backend.dto.meal;

import java.time.Instant;
import team.foodlog.backend.model.enums.MealAnalysisStatus;

public record MealEntryCreatedResponseDto(
        Long id,
        MealAnalysisStatus analysisStatus,
        Instant createdAt
) {}
