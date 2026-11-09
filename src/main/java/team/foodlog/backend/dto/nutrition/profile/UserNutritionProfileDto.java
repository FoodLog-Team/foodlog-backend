package team.foodlog.backend.dto.nutrition.profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record UserNutritionProfileDto(
        LocalDate dateOfBirth,
        Integer height,
        BigDecimal weight,
        Sex sex,
        ActivityLevel activityLevel,
        WeightGoal weightGoal
) {}
