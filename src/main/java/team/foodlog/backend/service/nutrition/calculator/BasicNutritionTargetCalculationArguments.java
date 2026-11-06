package team.foodlog.backend.service.nutrition.calculator;

import java.math.BigDecimal;

public record BasicNutritionTargetCalculationArguments(
        Integer basalMetabolicRate,
        BigDecimal activityLevelMultiplier,
        BigDecimal weightGoalMultiplier
) implements NutritionTargetCalculationArguments {}
