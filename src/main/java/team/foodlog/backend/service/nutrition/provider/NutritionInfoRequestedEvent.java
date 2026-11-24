package team.foodlog.backend.service.nutrition.provider;

import java.math.BigDecimal;
import java.util.Map;

public record NutritionInfoRequestedEvent(
        Long mealEntryId,
        Long userId,
        Map<String, BigDecimal> mealNameToWeight
) {}
