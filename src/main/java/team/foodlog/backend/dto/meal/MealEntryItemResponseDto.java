package team.foodlog.backend.dto.meal;

import java.math.BigDecimal;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

public record MealEntryItemResponseDto(
        String name,
        BigDecimal grams,
        NutritionInfo total,
        NutritionInfo per100g,
        MealItemFetchingStatus status
) {}
