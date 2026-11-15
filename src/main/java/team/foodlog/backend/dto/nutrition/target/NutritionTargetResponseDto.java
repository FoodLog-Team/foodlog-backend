package team.foodlog.backend.dto.nutrition.target;

import io.swagger.v3.oas.annotations.media.Schema;
import team.foodlog.backend.model.enums.TargetMode;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

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
        Integer fats,

        @Schema(
                description = """
                        Active nutrition target mode.

                        RECOMMENDED: Targets are calculated using the user's
                        nutrition profile.

                        CUSTOM: Targets are manually configured by the user.
                        """,
                example = "RECOMMENDED",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        TargetMode targetMode,

        @Schema(
                description = """
                        BMR formula used for calculating recommended nutrition targets.

                        Currently supported: MIFFLIN_ST_JEOR.
                        """,
                example = "MIFFLIN_ST_JEOR",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        NutritionFormulaType formulaType,

        @Schema(
                description = """
                        Strategy used to calculate daily nutrition targets.

                        Currently supported: BASIC.
                        """,
                example = "BASIC",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        NutritionTargetCalculationType calculationType

) {}
