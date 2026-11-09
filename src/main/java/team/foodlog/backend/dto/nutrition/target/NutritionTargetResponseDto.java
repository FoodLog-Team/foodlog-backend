package team.foodlog.backend.dto.nutrition.target;

public record NutritionTargetResponseDto(
        Integer kcal,
        Integer carbs,
        Integer protein,
        Integer fats
) {}
