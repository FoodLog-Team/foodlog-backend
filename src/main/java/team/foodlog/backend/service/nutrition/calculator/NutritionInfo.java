package team.foodlog.backend.service.nutrition.calculator;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Objects;

@Schema(
        description = """
                Energy and macronutrient values. Values are expressed per 100 grams or for
                the full item, depending on the parent response field.
                """
)
public record NutritionInfo(

        @Schema(description = "Energy in kilocalories (kcal).", example = "195.50")
        BigDecimal kcal,

        @Schema(description = "Carbohydrates in grams (g).", example = "0.0")
        BigDecimal carbs,

        @Schema(description = "Protein in grams (g).", example = "29.0")
        BigDecimal protein,

        @Schema(description = "Fat in grams (g).", example = "7.0")
        BigDecimal fat

) {

    public NutritionInfo {
        Objects.requireNonNull(kcal, "Kcal must not be null");
        Objects.requireNonNull(carbs, "Carbs must not be null");
        Objects.requireNonNull(protein, "Protein must not be null");
        Objects.requireNonNull(fat, "Fat must not be null");

        checkNonNegative(kcal, "Kcal");
        checkNonNegative(carbs, "Carbs");
        checkNonNegative(protein, "Protein");
        checkNonNegative(fat, "Fat");
    }

    private static void checkNonNegative(BigDecimal value, String name) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException(name + " cannot be negative: " + value);
        }
    }
}
