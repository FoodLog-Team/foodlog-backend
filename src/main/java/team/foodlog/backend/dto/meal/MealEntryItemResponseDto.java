package team.foodlog.backend.dto.meal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Schema(
        description = """
                Food item included in a meal entry, with its estimated weight and nutrition
                information.
                """
)
public record MealEntryItemResponseDto(

        @Schema(description = "Food name.", example = "Chicken, breast, grilled")
        String name,

        @Schema(description = "Estimated food weight in grams.", example = "150.0")
        BigDecimal grams,

        @Schema(description = "Calculated calories and macronutrients for the specified weight.")
        NutritionInfo total,

        @Schema(description = "Calories and macronutrients per 100 grams of the food.")
        NutritionInfo per100g,

        @Schema(
                description = "Nutrition lookup status: PENDING, COMPLETED, or FAILED.",
                example = "COMPLETED"
        )
        MealItemFetchingStatus status

) {}
