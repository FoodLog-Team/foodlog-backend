package team.foodlog.backend.dto.meal;

import java.time.Instant;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;

public record MealEntryItemCreatedResponseDto(
        Long mealEntryId,
        MealItemFetchingStatus status,
        Instant createdAt
) {}
