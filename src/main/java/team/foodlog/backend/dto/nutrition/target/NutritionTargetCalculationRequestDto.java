package team.foodlog.backend.dto.nutrition.target;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

@Schema(
        name = "NutritionTargetCalculationRequest",
        description = """
                Configuration for calculating recommended daily nutrition targets.

                Both the formula type and calculation type are required.
                """
)
public record NutritionTargetCalculationRequestDto(

        @NotNull
        @Schema(
                description = """
                        Formula used to calculate Basal Metabolic Rate (BMR).
                        Currently supported: MIFFLIN_ST_JEOR.
                        """,
                example = "MIFFLIN_ST_JEOR",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        NutritionFormulaType formulaType,

        @NotNull
        @Schema(
                description = """
                        Strategy used to calculate daily nutrition targets.
                        Currently supported: BASIC.
                        """,
                example = "BASIC",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        NutritionTargetCalculationType calculationType

) {
}
