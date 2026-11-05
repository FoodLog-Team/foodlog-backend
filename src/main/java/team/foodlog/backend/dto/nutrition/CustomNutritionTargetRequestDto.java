package team.foodlog.backend.dto.nutrition;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@GroupSequence({
        CustomNutritionTargetRequestDto.class,
        NutritionValidationGroup.class
})
@CaloriesConsistentWithMacros(groups = NutritionValidationGroup.class)
public record CustomNutritionTargetRequestDto(

        @NotNull
        @Min(800)
        @Max(8000)
        Integer calories,

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
