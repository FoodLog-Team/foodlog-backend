package team.foodlog.backend.dto.nutrition.target;

import jakarta.validation.constraints.NotNull;
import team.foodlog.backend.model.enums.TargetMode;

public record NutritionTargetModeRequestDto(@NotNull TargetMode mode) {}
