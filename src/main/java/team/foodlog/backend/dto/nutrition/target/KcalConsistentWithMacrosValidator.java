package team.foodlog.backend.dto.nutrition.target;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import team.foodlog.backend.config.NutritionConstants;

public class KcalConsistentWithMacrosValidator
        implements ConstraintValidator<KcalConsistentWithMacros,
                   CustomNutritionTargetRequestDto> {

    public static final BigDecimal DIFFERENCE_COEFFICIENT = BigDecimal.valueOf(0.05);

    @Override
    public boolean isValid(CustomNutritionTargetRequestDto request,
            ConstraintValidatorContext context) {
        BigDecimal requestedKcal = BigDecimal.valueOf(request.kcal());

        BigDecimal combinedKcal = BigDecimal.valueOf(request.carbs())
                    .multiply(NutritionConstants.KCAL_PER_GRAM_OF_CARBS)
                    .add(BigDecimal.valueOf(request.protein())
                    .multiply(NutritionConstants.KCAL_PER_GRAM_OF_PROTEIN))
                    .add(BigDecimal.valueOf(request.fats())
                    .multiply(NutritionConstants.KCAL_PER_GRAM_OF_FAT));

        double difference =
                Math.abs(bigDecimalToDouble(requestedKcal.subtract(combinedKcal)));
        double allowedDifference =
                bigDecimalToDouble(requestedKcal.multiply(DIFFERENCE_COEFFICIENT));

        return difference <= allowedDifference;
    }

    private double bigDecimalToDouble(BigDecimal value) {
        return value.doubleValue();
    }
}
