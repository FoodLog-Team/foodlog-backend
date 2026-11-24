package team.foodlog.backend.config;

import java.math.BigDecimal;

public final class NutritionConstants {

    public static final BigDecimal KCAL_PER_GRAM_OF_PROTEIN = BigDecimal.valueOf(4);

    public static final BigDecimal KCAL_PER_GRAM_OF_CARBS = BigDecimal.valueOf(4);

    public static final BigDecimal KCAL_PER_GRAM_OF_FAT = BigDecimal.valueOf(9);

    public static final BigDecimal CARBS_RATIO = BigDecimal.valueOf(0.5);

    public static final BigDecimal PROTEIN_RATIO = BigDecimal.valueOf(0.3);

    public static final BigDecimal FATS_RATIO = BigDecimal.valueOf(0.2);

    private NutritionConstants() {
    }

}
