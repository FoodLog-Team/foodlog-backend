package team.foodlog.backend.dto.meal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import team.foodlog.backend.model.enums.AiConfidence;
import team.foodlog.backend.model.enums.MealAnalysisStatus;
import team.foodlog.backend.model.enums.MealEntryProcessingError;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Schema(
        description = """
                Current state of a meal entry, including its food items, total weight,
                and calculated nutrition values.
                """
)
public record MealEntryResponseDto(

        @Schema(description = "ID of the meal entry.", example = "1")
        Long id,

        @Schema(
                description = """
                        Current processing status: PENDING, PROCESSING, COMPLETED, or FAILED.
                        """,
                example = "COMPLETED"
        )
        MealAnalysisStatus status,

        @Schema(
                description = """
                        Processing error. Null when the meal entry has no processing error.
                        """,
                example = "NUTRITION_FETCHING_FAILED",
                nullable = true
        )
        MealEntryProcessingError processingError,

        @Schema(
                description = "Sum of the weights of all food items in grams.",
                example = "350.0"
        )
        BigDecimal grams,

        @Schema(
                description = "Confidence of the AI food recognition (recognition only).",
                example = "HIGH"
        )
        AiConfidence aiConfidence,

        @Schema(
                description = """
                        Total calculated calories and macronutrients for the meal entry.
                        """
        )
        NutritionInfo total,

        @Schema(description = "Food items associated with this meal entry.")
        List<MealEntryItemResponseDto> items

) {}
