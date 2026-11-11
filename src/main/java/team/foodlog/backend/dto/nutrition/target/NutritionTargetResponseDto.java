package team.foodlog.backend.dto.nutrition.target;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Response containing the user's current daily nutrition targets.
                Values represent either the recommended targets (calculated from the user's profile)
                or the custom targets (manually set by the user),
                depending on the active target mode."""
)
public record NutritionTargetResponseDto(

        @Schema(
                description = "Daily calorie target in kilocalories (kcal).",
                example = "2200"
        )
        Integer kcal,

        @Schema(
                description = "Daily carbohydrate target in grams (g).",
                example = "250"
        )
        Integer carbs,

        @Schema(
                description = "Daily protein target in grams (g).",
                example = "150"
        )
        Integer protein,

        @Schema(
                description = "Daily fat target in grams (g).",
                example = "80"
        )
        Integer fats
) {
}
