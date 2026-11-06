package team.foodlog.backend.dto.nutrition.profile;

import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record UserNutritionProfileResponseDto(
        Long userId,
        Integer height,
        Integer weight,
        Sex sex,
        ActivityLevel activityLevel,
        WeightGoal weightGoal
) {}
