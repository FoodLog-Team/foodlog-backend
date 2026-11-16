package team.foodlog.backend.dto.nutrition.target;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = """
                Request payload for setting custom daily nutrition targets.
                All values are in grams (g).""",
        example = """
                {"carbs": 250, "protein": 150, "fats": 80}"""
)
public record CustomNutritionTargetRequestDto(

        @Schema(
                description = """
                        Daily carbohydrate target in grams (g). Must be between 1 and 1000.
                        Each gram of carbs provides approximately 4 kcal.""",
                example = "250",
                minimum = "1",
                maximum = "1000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Min(1)
        @Max(1000)
        Integer carbs,

        @Schema(
                description = """
                        Daily protein target in grams (g). Must be between 1 and 500.
                        Each gram of protein provides approximately 4 kcal.""",
                example = "150",
                minimum = "1",
                maximum = "500",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Min(1)
        @Max(500)
        Integer protein,

        @Schema(
                description = """
                        Daily fat target in grams (g). Must be between 1 and 500.
                        Each gram of fat provides approximately 9 kcal.""",
                example = "80",
                minimum = "1",
                maximum = "500",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Min(1)
        @Max(500)
        Integer fats

) {
}
