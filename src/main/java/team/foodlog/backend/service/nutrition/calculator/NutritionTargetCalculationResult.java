package team.foodlog.backend.service.nutrition.calculator;

public record NutritionTargetCalculationResult(
        Integer kcal,
        Integer carbs,
        Integer protein,
        Integer fats
) {}
