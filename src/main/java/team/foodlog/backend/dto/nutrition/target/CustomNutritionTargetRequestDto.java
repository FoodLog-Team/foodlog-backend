package team.foodlog.backend.dto.nutrition.target;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import team.foodlog.backend.dto.nutrition.profile.CustomNutritionTargetChecks;

@GroupSequence({
        CustomNutritionTargetRequestDto.class,
        CustomNutritionTargetChecks.class
})
@KcalConsistentWithMacros(groups = CustomNutritionTargetChecks.class)
public record CustomNutritionTargetRequestDto(

        @NotNull
        @Min(800)
        @Max(8000)
        Integer kcal,

        @NotNull
        @Min(1)
        @Max(1000)
        Integer carbs,

        @NotNull
        @Min(1)
        @Max(500)
        Integer protein,

        @NotNull
        @Min(1)
        @Max(500)
        Integer fats

) {}
