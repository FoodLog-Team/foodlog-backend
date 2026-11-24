package team.foodlog.backend.service.nutrition.calculator;

import java.math.BigDecimal;
import java.util.Objects;

public record NutritionInfo(
        BigDecimal kcal,
        BigDecimal carbs,
        BigDecimal protein,
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
