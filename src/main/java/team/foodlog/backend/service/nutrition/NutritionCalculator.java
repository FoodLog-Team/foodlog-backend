package team.foodlog.backend.service.nutrition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

public class NutritionCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public static NutritionInfo calculateTotal(
            BigDecimal grams, NutritionInfo nutritionPer100g
    ) {
        Objects.requireNonNull(nutritionPer100g, "Nutrition must not be null");
        checkGrams(grams);

        return new NutritionInfo(
                calculateTotal(grams, nutritionPer100g.kcal()),
                calculateTotal(grams, nutritionPer100g.carbs()),
                calculateTotal(grams, nutritionPer100g.protein()),
                calculateTotal(grams, nutritionPer100g.fat())
        );
    }

    public static NutritionInfo calculateTotal(List<NutritionInfo> nutritions) {
        return nutritions.stream()
                .reduce(new NutritionInfo(ZERO, ZERO, ZERO, ZERO),
                        (a, b) -> new NutritionInfo(
                                a.kcal().add(b.kcal()),
                                a.carbs().add(b.carbs()),
                                a.protein().add(b.protein()),
                                a.fat().add(b.fat())
                        )
                );
    }

    private static BigDecimal calculateTotal(BigDecimal grams, BigDecimal nutrition) {
        return nutrition.multiply(grams)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    public static NutritionInfo calculatePer100g(
            BigDecimal grams, NutritionInfo totalNutrition
    ) {
        Objects.requireNonNull(totalNutrition, "Nutrition must not be null");
        checkGrams(grams);

        return new NutritionInfo(
                calculatePer100g(grams, totalNutrition.kcal()),
                calculatePer100g(grams, totalNutrition.carbs()),
                calculatePer100g(grams, totalNutrition.protein()),
                calculatePer100g(grams, totalNutrition.fat())
        );
    }

    private static BigDecimal calculatePer100g(BigDecimal grams, BigDecimal nutrition) {
        return nutrition.multiply(HUNDRED)
                .divide(grams, 2, RoundingMode.HALF_UP);
    }

    private static void checkGrams(BigDecimal grams) {
        Objects.requireNonNull(grams, "Grams must not be null");

        if (grams.signum() <= 0) {
            throw new IllegalArgumentException("Grams must be positive: " + grams);
        }
    }

}
