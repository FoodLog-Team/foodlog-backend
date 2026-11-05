package team.foodlog.backend.dto.nutrition;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CaloriesConsistentWithMacrosValidator
        implements ConstraintValidator<CaloriesConsistentWithMacros,
                   CustomNutritionTargetRequestDto> {

    public static final int CALORIES_PER_CARB = 4;
    public static final int CALORIES_PER_PROTEIN = 4;
    public static final int CALORIES_PER_FAT = 9;

    public static final double DIFFERENCE_COEFFICIENT = 0.05;

    @Override
    public boolean isValid(CustomNutritionTargetRequestDto request,
            ConstraintValidatorContext context) {
        int requestedCalories = request.calories();

        int combinedCalories =
                request.carbs() * CALORIES_PER_CARB
                + request.protein() * CALORIES_PER_PROTEIN
                + request.fats() * CALORIES_PER_FAT;

        double difference = Math.abs(requestedCalories - combinedCalories);
        double allowedDifference = requestedCalories * DIFFERENCE_COEFFICIENT;

        return difference <= allowedDifference;
    }
}
